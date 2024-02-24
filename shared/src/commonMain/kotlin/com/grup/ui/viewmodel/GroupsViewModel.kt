package com.grup.ui.viewmodel

import com.grup.exceptions.UserNotInGroupException
import com.grup.models.Group
import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

internal class GroupsViewModel : LoggedInViewModel() {
    public override val userObject: User
        get() = super.userObject

    private val _userInfosFlow = apiServer.getAllUserInfosAsFlow()
    val userInfosFlow: StateFlow<List<UserInfo>> = _userInfosFlow.asState()

    private val _groupsFlow = apiServer.getAllGroupsAsFlow()
    val groups: StateFlow<List<Group>> = _groupsFlow.combine(userInfosFlow) { groups, userInfos ->
        groups.sortedBy { group ->
            userInfos.find { it.group.id == group.id }?.joinDate
        }
    }.asState()

    fun selectGroup(groupId: String, onSuccess: () -> Unit) {
        if (groups.value.any { it.id == groupId }) {
            selectedGroupId = groupId
            onSuccess()
        } else {
            throw UserNotInGroupException()
        }
    }
}