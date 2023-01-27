package com.grup.android

import com.grup.APIServer
import com.grup.android.transaction.AcceptDebtAction
import com.grup.android.transaction.CreateDebtAction
import com.grup.android.transaction.TransactionActivity
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.models.*
import kotlinx.coroutines.flow.*

class MainViewModel : ViewModel() {
    companion object {
        private val selectedGroupMutable:
                MutableStateFlow<Group?> = MutableStateFlow(null)
        val selectedGroup: StateFlow<Group?> = selectedGroupMutable
    }
    val hasUserObject: Boolean
        get() = try {
            userObject
            true
        } catch (e: UserObjectNotFoundException) {
            false
        }

    // Selected group in the UI. Other UI flows use this to filter data based on the selected group.
    val selectedGroup
        get() = MainViewModel.selectedGroup

    fun onSelectedGroupChange(group: Group) = group.also {
        selectedGroupMutable.value = group
    }

    // Hot flow containing all Group's the user is in. Updates selectedGroup if it's changed/deleted
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
    private val _myUserInfos = APIServer.getMyUserInfosAsFlow()
    val myUserInfo: StateFlow<UserInfo?> = _myUserInfos.map { userInfos ->
        userInfos.find { it.userId == userObject.getId() }
    }.asState()

    val groupInvitesList: StateFlow<List<GroupInvite>> = APIServer.getAllGroupInvitesAsFlow()
        .asNotifications()

    // Hot flow containing DebtAction's belonging to the selectedGroup
    private val _debtActionsFlow = APIServer.getAllDebtActionsAsFlow()
    private val debtActions: StateFlow<List<DebtAction>> =
        _debtActionsFlow.combine(selectedGroup) { debtActions, selectedGroup ->
            selectedGroup?.let { group ->
                debtActions.filter { debtAction ->
                    debtAction.groupId == group.getId()
                }
            } ?: emptyList()
        }.asNotifications()

    // Hot flow containing all DebtActions across all groups that the user is a part of
    val subscribedDebtActionsAsNotifications: StateFlow<List<DebtAction>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.debtee == APIServer.user.getId() || debtAction.debtTransactions.any {
                    it.debtor == APIServer.user.getId()
                }
            }
        }.asNotifications()

    // DebtActions belonging to the selectedGroup, mapped to TransactionActivity(s)
    private val debtActionsAsTransactionActivityFlow: Flow<List<TransactionActivity>> =
        debtActions.map { debtActions ->
            debtActions.map { debtAction ->
                listOf(
                    CreateDebtAction(debtAction),
                    *debtAction.debtTransactions.filter { transactionRecord ->
                        transactionRecord.dateAccepted != "PENDING"
                    }.map { transactionRecord ->
                        AcceptDebtAction(debtAction, transactionRecord)
                    }.toTypedArray()
                )
            }.flatten()
        }

    // Hot flow combining all TransactionActivity flows to be displayed as recent activity in UI
    val groupActivity: StateFlow<List<TransactionActivity>> =
        debtActionsAsTransactionActivityFlow.map { transactionActivities ->
            transactionActivities.sortedBy { transactionActivity ->
                transactionActivity.date
            }
        }.asState()


    // Group operations
    fun createGroup(groupName: String) = APIServer.createGroup(groupName)
    fun acceptInviteToGroup(groupInvite: GroupInvite) = APIServer.acceptInviteToGroup(groupInvite)
    fun inviteUserToGroup(username: String, group: Group) =
        APIServer.inviteUserToGroup(username, group)

    fun logOut() = APIServer.logOut()
}