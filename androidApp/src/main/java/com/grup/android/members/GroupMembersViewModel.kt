package com.grup.android.members

import androidx.lifecycle.viewModelScope
import com.grup.APIServer
import com.grup.android.MainViewModel
import com.grup.android.ViewModel
import com.grup.exceptions.APIException
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GroupMembersViewModel : ViewModel() {
    private val selectedGroup
        get() = MainViewModel.selectedGroup

    // Hot flow containing UserInfo's belonging to the selectedGroup. Assumes selectedGroup does not
    // change during lifecycle.
    private val _userInfosFlow = APIServer.getAllUserInfosAsFlow()
    val userInfos: StateFlow<List<UserInfo>> =
        _userInfosFlow.map { userInfos ->
            userInfos.filter { userInfo ->
                userInfo.groupId == selectedGroup.getId()
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
        viewModelScope.launch {
            try {
                APIServer.inviteUserToGroup(username, selectedGroup)
                _inviteResult.value = InviteResult.Sent
            } catch (e: APIException) {
                _inviteResult.value = InviteResult.Error(e)
            }
        }
    }
}