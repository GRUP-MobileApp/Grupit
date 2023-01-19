package com.grup.android

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.grup.APIServer
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.models.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

class MainViewModel : ViewModel() {
    private companion object {
        private const val STOP_TIMEOUT_MILLIS: Long = 5000
    }

    val hasUserObject: Boolean
        get() = try {
            APIServer.user
            true
        } catch (e: UserObjectNotFoundException) {
            false
        }

    var selectedGroup: MutableStateFlow<Group?> = MutableStateFlow(null)

    private val _groupsFlow = APIServer.getAllGroupsAsFlow().onEach { newGroups ->
        selectedGroup.value?.let { nonNullGroup ->
            selectedGroup.value = newGroups.find { group ->
                group.getId() == nonNullGroup.getId()
            }
        } ?: run {
            selectedGroup.value = newGroups.getOrNull(0)
        }
    }
    val groups: StateFlow<List<Group>> = _groupsFlow.asState()

    private val _userInfosFlow = APIServer.getAllUserInfosAsFlow()
    val userInfos: StateFlow<List<UserInfo>> =
        _userInfosFlow.map { userInfos ->
            selectedGroup.value?.let { group ->
                userInfos.filter { userInfo ->
                    userInfo.groupId == group.getId()
                }
            } ?: emptyList()
        }.asState()

    val groupInvitesList: StateFlow<List<GroupInvite>> = APIServer.getAllGroupInvitesAsFlow()
        .asNotifications()

    private val _debtActionsFlow = APIServer.getAllDebtActionsAsFlow()
    val debtActions: StateFlow<List<DebtAction>> =
        _debtActionsFlow.map { debtActions ->
            selectedGroup.value?.let { group ->
                debtActions.filter { debtAction ->
                    debtAction.groupId == group.getId()
                }
            } ?: emptyList()
        }.asNotifications()
    val subscribedDebtActions: StateFlow<List<DebtAction>> = _debtActionsFlow.map { debtActions ->
        debtActions.filter { debtAction ->
            debtAction.debtee == APIServer.user.getId() || debtAction.debtTransactions.any {
                it.debtor == APIServer.user.getId()
            }
        }
    }.asNotifications()

    fun createGroup(groupName: String) = APIServer.createGroup(groupName)
    fun acceptInviteToGroup(groupInvite: GroupInvite) = APIServer.acceptInviteToGroup(groupInvite)
    fun inviteUserToGroup(username: String, group: Group) =
        APIServer.inviteUserToGroup(username, group)

    fun createDebtAction(debtAmounts: Map<String, Double>) =
        selectedGroup.value?.let { selectedGroup ->
            APIServer.createDebtAction(
                debtAmounts.map { (userId, balanceChange) ->
                    TransactionRecord().apply {
                        this.debtor = userId
                        this.balanceChange = balanceChange
                    }
                },
                selectedGroup
            )
        }

    private fun <T> Flow<List<T>>.asNotifications() =
        this.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private fun <T> Flow<T>.asState() =
        this.let { flow ->
            runBlocking { flow.first() }.let { initialList ->
                flow.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialList
                )
            }
        }
}