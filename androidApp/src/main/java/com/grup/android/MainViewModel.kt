package com.grup.android

import com.grup.APIServer
import com.grup.android.transaction.TransactionActivity
import com.grup.models.*
import kotlinx.coroutines.flow.*

class MainViewModel : ViewModel() {
    val username: String
        get() = userObject.username!!

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
    private val _groupsFlow = APIServer.getAllGroupsAsFlow()
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
    private val _myUserInfosFlow by lazy { APIServer.getMyUserInfosAsFlow() }
    val myUserInfo: StateFlow<UserInfo?> by lazy {
        _myUserInfosFlow.map { userInfos ->
            userInfos.find { it.userId == userObject.getId() }
        }.asState()
    }

    // DebtActions belonging to the selectedGroup, mapped to TransactionActivity
    private val _debtActionsFlow = APIServer.getAllDebtActionsAsFlow()
        .combine(selectedGroup) { debtActions, selectedGroup ->
            selectedGroup?.let { group ->
                debtActions.filter { debtAction ->
                    debtAction.groupId == group.getId()
                }
            } ?: emptyList()
        }
    private val debtActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.flatMap { debtAction ->
                listOf(
                    TransactionActivity.CreateDebtAction(debtAction),
                    *debtAction.debtTransactions.filter { transactionRecord ->
                        transactionRecord.dateAccepted != TransactionRecord.PENDING
                    }.map { transactionRecord ->
                        TransactionActivity.AcceptDebtAction(debtAction, transactionRecord)
                    }.toTypedArray()
                )
            }
        }

    // SettleActions belonging to the selectedGroup, mapped to TransactionActivity
    private val _settleActionsFlow = APIServer.getAllSettleActionsAsFlow()
        .combine(selectedGroup) { settleActions, selectedGroup ->
            selectedGroup?.let { group ->
                settleActions.filter { settleAction ->
                    settleAction.groupId == group.getId()
                }
            } ?: emptyList()
        }
    val activeSettleActions: StateFlow<List<SettleAction>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.remainingAmount > 0
            }
        }.asState()

    private val settleActionsAsTransactionActivity: Flow<List<TransactionActivity>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.flatMap { settleAction ->
                listOf(
                    TransactionActivity.CreateSettleAction(settleAction),
                    *settleAction.debtTransactions.filter { transactionRecord ->
                        transactionRecord.dateAccepted != TransactionRecord.PENDING
                    }.map { transactionRecord ->
                        TransactionActivity.SettlePartialSettleAction(
                            settleAction,
                            transactionRecord
                        )
                    }.toTypedArray()
                )
            }
        }

    // Hot flow combining all TransactionActivity flows to be displayed as recent activity in UI
    val groupActivity: StateFlow<List<TransactionActivity>> =
        combine(
            debtActionsAsTransactionActivity,
            settleActionsAsTransactionActivity
        ) { allTransactionActivities: Array<List<TransactionActivity>> ->
            allTransactionActivities.flatMap { it }.sortedBy { transactionActivity ->
                transactionActivity.date
            }
        }.asInitialEmptyState()


    // Group operations
    fun createGroup(groupName: String) = APIServer.createGroup(groupName)

    fun logOut() = APIServer.logOut()
}