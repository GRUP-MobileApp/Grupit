package com.grup.ui.viewmodel

import com.grup.models.Group
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.ui.models.TransactionActivity
import com.rickclephas.kmm.viewmodel.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : LoggedInViewModel() {
    companion object {
        private val selectedGroupMutable:
                MutableStateFlow<Group?> = MutableStateFlow(null)
        val selectedGroup: Group
            get() = selectedGroupMutable.value!!
    }

    // Selected group in the UI. Other UI flows use this to filter data based on the selected group.
    val selectedGroup: StateFlow<Group?> = selectedGroupMutable
    fun onSelectedGroupChange(group: Group) = group.also {
        selectedGroupMutable.value = group
    }

    // Hot flow containing all Groups the user is in. Updates selectedGroup if it's changed/deleted
    private val _groupsFlow = apiServer.getAllGroupsAsFlow()
    val groups: StateFlow<List<Group>> = _groupsFlow.onEach { newGroups ->
        selectedGroup.value?.let { nonNullGroup ->
            selectedGroupMutable.value = newGroups.find { group ->
                group.getId() == nonNullGroup.getId()
            }
        } ?: run {
            selectedGroupMutable.value = newGroups.getOrNull(0)
        }
    }.asState()

    // Hot flow containing User's UserInfos
    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    val myUserInfo: StateFlow<UserInfo?> =
        _myUserInfosFlow.combine(selectedGroup) { userInfos, selectedGroup ->
            selectedGroup?.let { nonNullGroup ->
                userInfos.find { userInfo ->
                    userInfo.groupId == nonNullGroup.getId()
                }
            }
        }.asState()

    // DebtActions belonging to the selectedGroup, mapped to TransactionActivity
    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
        .combine(selectedGroup) { debtActions, selectedGroup ->
            selectedGroup?.let { group ->
                debtActions.filter { debtAction ->
                    debtAction.groupId == group.getId()
                }
            } ?: emptyList()
        }
    private val debtActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.totalAmount > 0
            }.flatMap { debtAction ->
                listOf(
                    TransactionActivity.CreateDebtAction(debtAction),
                    *debtAction.transactionRecords.filter { transactionRecord ->
                        transactionRecord.isAccepted
                    }.map { transactionRecord ->
                        TransactionActivity.AcceptDebtAction(debtAction, transactionRecord)
                    }.toTypedArray()
                )
            }
        }

    // SettleActions belonging to the selectedGroup, mapped to TransactionActivity
    private val _settleActionsFlow = apiServer.getAllSettleActionsAsFlow()
        .combine(selectedGroup) { settleActions, selectedGroup ->
            selectedGroup?.let { group ->
                settleActions.filter { settleAction ->
                    settleAction.groupId == group.getId()
                }
            } ?: emptyList()
        }
    // Active SettleActions to be displayed, oldest first
    val activeSettleActions: StateFlow<List<SettleAction>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.remainingAmount > 0
            }.sortedBy { it.date }
        }.asState()

    private val completedSettleActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.remainingAmount == 0.0
            }.map { settleAction ->
                TransactionActivity.CreateSettleAction(settleAction)
            }
        }
    private val settleActionTransactionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.flatMap { settleAction ->
                settleAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.isAccepted
                }.map { transactionRecord ->
                    TransactionActivity.SettlePartialSettleAction(
                        settleAction,
                        transactionRecord
                    )
                }
            }
        }

    // Hot flow combining all TransactionActivity flows to be displayed as recent activity in UI
    val groupActivity: StateFlow<List<TransactionActivity>> =
        combine(
            debtActionsAsTransactionActivity,
            completedSettleActionsAsTransactionActivity,
            settleActionTransactionsAsTransactionActivity
        ) { allTransactionActivities: Array<List<TransactionActivity>> ->
            allTransactionActivities.flatMap { it }.sortedByDescending { transactionActivity ->
                transactionActivity.date
            }
        }.asInitialEmptyState()


    // Group
    fun createGroup(groupName: String) = apiServer.createGroup(groupName)

    // SettleAction
    fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = viewModelScope.coroutineScope.launch {
        apiServer.acceptSettleActionTransaction(settleAction, transactionRecord)
    }

    fun logOut() = viewModelScope.coroutineScope.launch {
        selectedGroupMutable.value = null
        closeApiServer()
    }
}