package com.grup.android

import androidx.lifecycle.viewModelScope
import com.grup.APIServer
import com.grup.models.UserInfo
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

    fun inviteUserToGroup(username: String) = viewModelScope.launch {
        APIServer.inviteUserToGroup(username, selectedGroup)
    }
}