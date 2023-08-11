package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.exceptions.APIException
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class GroupMembersViewModel : LoggedInViewModel() {
    private val selectedGroup
        get() = MainViewModel.selectedGroup

    // Hot flow containing UserInfo's belonging to the selectedGroup. Assumes selectedGroup does not
    // change during lifecycle.
    private val _userInfosFlow = apiServer.getAllUserInfosAsFlow()
    val userInfos: StateFlow<List<UserInfo>> =
        _userInfosFlow.map { userInfos ->
            userInfos.filter { userInfo ->
                userInfo.groupId == selectedGroup.id
            }.sortedBy { userInfo ->
                if (userInfo.user.id == userObject.id) "" else userInfo.user.displayName
            }
        }.asState()

    sealed class InviteResult {
        object Sent : InviteResult()
        object Pending : InviteResult()
        data class Error(val exception: Exception) : InviteResult()
        object None : InviteResult()
    }

    private val _inviteResult = MutableStateFlow<InviteResult>(InviteResult.None)
    val inviteResult: StateFlow<InviteResult> = _inviteResult

    fun resetInviteResult() {
        _inviteResult.value = InviteResult.None
    }

    fun inviteUserToGroup(username: String) {
        _inviteResult.value = InviteResult.Pending
        coroutineScope.launch {
            try {
                apiServer.inviteUserToGroup(username, selectedGroup)
                _inviteResult.value = InviteResult.Sent
            } catch (e: APIException) {
                _inviteResult.value = InviteResult.Error(e)
            }
        }
    }
}