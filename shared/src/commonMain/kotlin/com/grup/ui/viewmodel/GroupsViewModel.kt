package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.exceptions.APIException
import com.grup.models.Group
import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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
    }.asInitialEmptyState()

    fun selectGroup(group: Group) {
        selectedGroup = group
    }
}