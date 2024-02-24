package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.exceptions.APIException
import com.grup.exceptions.UserNotInGroupException
import com.grup.models.Action
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.SettleAction
import com.grup.models.UserInfo
import com.grup.ui.models.TransactionActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class GroupDetailsViewModel : LoggedInViewModel() {
    private val _groups = apiServer.getAllGroupsAsFlow()
    val selectedGroup: StateFlow<Group> = _groups.map { groups ->
        groups.find { it.id == selectedGroupId } ?: throw UserNotInGroupException()
    }.asState()

    // Hot flow containing User's UserInfos
    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    val myUserInfo: StateFlow<UserInfo?> =
        _myUserInfosFlow.map { userInfos ->
            userInfos.find { userInfo ->
                userInfo.group.id == selectedGroupId
            }
        }.asState()

    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
        .map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.group.id == selectedGroupId
            }
        }

    // SettleActions belonging to the selectedGroup, mapped to TransactionActivity
    private val _settleActionsFlow = apiServer.getAllSettleActionsAsFlow()
        .map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.group.id == selectedGroupId
            }
        }

    // Incoming SettleActions to be displayed, oldest first
    val incomingActions: StateFlow<List<Action>> =
        combine(
            _debtActionsFlow.map { debtActions ->
                debtActions.filter { debtAction ->
                    debtAction.transactionRecords.any { transactionRecord ->
                        transactionRecord.userInfo.user.id == userObject.id &&
                                !transactionRecord.isAccepted
                    }
                }
            },
            _settleActionsFlow.map { settleActions ->
                settleActions.filter { settleAction ->
                    settleAction.transactionRecords.any { transactionRecord ->
                        transactionRecord.userInfo.user.id == userObject.id &&
                                !transactionRecord.isAccepted
                    }
                }
            }
        ) { actions: Array<List<Action>> ->
            actions.flatMap { it }.sortedBy { it.date }
        }.asInitialEmptyState()


    // DebtActions belonging to the selectedGroup, mapped to TransactionActivity
    private val debtActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.filter { debtAction ->
                !incomingActions.value.any { it.id == debtAction.id }
            }.map { debtAction ->
                TransactionActivity.CreateDebtAction(debtAction)
            }
        }

    private val settleActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                !incomingActions.value.any { it.id == settleAction.id }
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

    // DebtAction
    fun acceptAction(
        action: Action,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = screenModelScope.launch {
        try {
            action.transactionRecords.find {
                it.userInfo.user.id == userObject.id
            }?.let { transactionRecord ->
                when (action) {
                    is DebtAction -> apiServer.acceptDebtAction(action, transactionRecord)
                    is SettleAction -> apiServer.acceptSettleAction(action, transactionRecord)
                }
            } ?: throw object : APIException("") { }
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }

    fun findActionById(actionId: String, onSuccess:(Action) -> Unit) {
        screenModelScope.launch {
            combine(_debtActionsFlow, _settleActionsFlow) { allActions: Array<List<Action>> ->
                allActions.flatMap { it }
            }.first().find { it.id == actionId }?.let(onSuccess)
        }
    }

    override fun onDispose() {
        super.onDispose()
        selectedGroupId = null
    }
}