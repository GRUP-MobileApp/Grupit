package com.grup.ui.viewmodel

import com.grup.exceptions.UserNotInGroupException
import com.grup.models.DebtAction
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.other.FirstTimeSettings
import com.grup.ui.models.TransactionActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal class GroupDetailsViewModel(val selectedGroupId: String) : LoggedInViewModel() {
    var hasViewedTutorial: Boolean
        get() = FirstTimeSettings.hasViewedTutorial
        set(value) { FirstTimeSettings.hasViewedTutorial = value }

    // Hot flow containing User's UserInfos
    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    val myUserInfo: StateFlow<UserInfo> =
        _myUserInfosFlow.map { userInfos ->
            userInfos.find { userInfo ->
                userInfo.group.id == selectedGroupId
            } ?: throw UserNotInGroupException()
        }.asState()

    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
        .map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.userInfo.group.id == selectedGroupId
            }
        }

    // SettleActions belonging to the selectedGroup, mapped to TransactionActivity
    private val _settleActionsFlow = apiServer.getAllSettleActionsAsFlow()
        .map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.userInfo.group.id == selectedGroupId
            }
        }

    // Active SettleActions to be displayed
    val activeSettleActions: StateFlow<List<SettleAction>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                !settleAction.isCompleted && settleAction.remainingAmount > 0
            }.sortedWith(
                compareByDescending<SettleAction> { settleAction ->
                    settleAction.userInfo.user.id == userObject.id
                }.thenBy { settleAction ->
                    settleAction.date
                }
            )
        }.asInitialEmptyState()

    // Incoming DebtActions to be displayed
    val incomingDebtActions: StateFlow<List<Pair<DebtAction, TransactionRecord>>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.mapNotNull { debtAction ->
                debtAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.userInfo.user.id == userObject.id &&
                            transactionRecord.status is TransactionRecord.Status.Pending
                }?.let { transactionRecord ->
                    Pair(debtAction, transactionRecord)
                }
            }.sortedByDescending { it.first.date }
        }.asInitialEmptyState()

    // DebtActions belonging to the selectedGroup, mapped to TransactionActivity
    private val debtActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.filter { debtAction ->
                !incomingDebtActions.value.map { it.first }.contains(debtAction)
            }.map { debtAction ->
                TransactionActivity.CreateDebtAction(debtAction)
            }
        }

    private val completedSettleActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.isCompleted
            }.map { settleAction ->
                TransactionActivity.CompletedSettleAction(settleAction)
            }
        }

    // Hot flow combining all TransactionActivity flows to be displayed as recent activity in UI
    val groupActivity: StateFlow<List<TransactionActivity>> =
        combine(
            debtActionsAsTransactionActivity,
            completedSettleActionsAsTransactionActivity
        ) { allTransactionActivities: Array<List<TransactionActivity>> ->
            allTransactionActivities.flatMap { it }.sortedByDescending { transactionActivity ->
                transactionActivity.date
            }
        }.asInitialEmptyState()
}