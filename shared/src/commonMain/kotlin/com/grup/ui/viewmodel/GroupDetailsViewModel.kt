package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.ui.models.TransactionActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class GroupDetailsViewModel : LoggedInViewModel() {
    // Hot flow containing User's UserInfos
    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    val myUserInfo: StateFlow<UserInfo?> =
        _myUserInfosFlow.map { userInfos ->
            userInfos.find { userInfo ->
                userInfo.group.id == selectedGroup?.id
            }
        }.asState()

    // DebtActions belonging to the selectedGroup, mapped to TransactionActivity
    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
        .map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.group.id == selectedGroup?.id
            }
        }
    private val debtActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.totalAmount > 0
            }.map { debtAction ->
                TransactionActivity.CreateDebtAction(debtAction)
            }
        }

    // SettleActions belonging to the selectedGroup, mapped to TransactionActivity
    private val _settleActionsFlow = apiServer.getAllSettleActionsAsFlow()
        .map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.group.id == selectedGroup?.id
            }
        }
    // Incoming SettleActions to be displayed, oldest first
    val incomingSettleActions: StateFlow<List<SettleAction>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.transactionRecords.any { transactionRecord ->
                    transactionRecord.userInfo.user.id == userObject.id &&
                            !transactionRecord.isAccepted
                }
            }.sortedBy { it.date }
        }.asState()

    private val settleActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                !incomingSettleActions.value.contains(settleAction)
            }.map { settleAction ->
                TransactionActivity.CreateSettleAction(settleAction)
            }
        }

    // Hot flow combining all TransactionActivity flows to be displayed as recent activity in UI
    val groupActivity: StateFlow<List<TransactionActivity>> =
        combine(
            debtActionsAsTransactionActivity,
            settleActionsAsTransactionActivity
        ) { allTransactionActivities: Array<List<TransactionActivity>> ->
            allTransactionActivities.flatMap { it }.sortedByDescending { transactionActivity ->
                transactionActivity.date
            }
        }.asInitialEmptyState()

    // SettleAction
    fun acceptSettleAction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = screenModelScope.launch {
        apiServer.acceptSettleAction(settleAction, transactionRecord)
    }
}