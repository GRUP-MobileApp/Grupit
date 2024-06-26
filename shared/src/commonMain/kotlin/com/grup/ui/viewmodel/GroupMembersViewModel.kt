package com.grup.ui.viewmodel

import com.grup.exceptions.APIException
import com.grup.exceptions.UserNotInGroupException
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

internal class GroupMembersViewModel(private val selectedGroupId: String) : LoggedInViewModel() {
    // Hot flow containing UserInfo's belonging to the selectedGroup. Assumes selectedGroup does not
    // change during lifecycle.
    private val _userInfosFlow = apiServer.getAllUserInfosAsFlow()
    val userInfos: StateFlow<List<UserInfo>> =
        _userInfosFlow.map { userInfos ->
            userInfos.filter { userInfo ->
                userInfo.group.id == selectedGroupId
            }.sortedBy { userInfo ->
                if (userInfo.user.id == userId) "" else userInfo.user.displayName
            }
        }.asState()

    sealed class InviteResult {
        data object Sent : InviteResult()
        data object Pending : InviteResult()
        data class Error(val exception: Exception) : InviteResult()
        data object None : InviteResult()
    }

    private val _inviteResult = MutableStateFlow<InviteResult>(InviteResult.None)
    val inviteResult: StateFlow<InviteResult> = _inviteResult

    fun resetInviteResult() {
        _inviteResult.value = InviteResult.None
    }

    fun createGroupInvite(username: String) {
        _inviteResult.value = InviteResult.Pending
        launchJob {
            try {
                userInfos.value.find { it.user.id == userId}?.let { myUserInfo ->
                    apiServer.createGroupInvite(myUserInfo, username)
                    _inviteResult.value = InviteResult.Sent
                } ?: throw UserNotInGroupException()
            } catch (e: APIException) {
                _inviteResult.value = InviteResult.Error(e)
            }
        }
    }

    fun leaveGroup(onSuccess: () -> Unit, onError: (String?) -> Unit) = launchJob {
        try {
            userInfos.value.find { userInfo ->
                userInfo.user.id == userId
            }?.let { userInfo ->
                apiServer.leaveGroup(userInfo)
                onSuccess()
            } ?: onError("Internal error, try again later")
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}