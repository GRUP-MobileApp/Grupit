package com.grup.ui.viewmodel

import com.grup.models.Group
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach

internal class GroupsViewModel : LoggedInViewModel() {

    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    val myUserInfosFlow: StateFlow<List<UserInfo>> = _myUserInfosFlow.asState()

    private val _groupsFlow = apiServer.getAllGroupsAsFlow()
    val groups: StateFlow<List<Group>> = _groupsFlow.combine(myUserInfosFlow) { groups, userInfos ->
        groups.sortedBy { group ->
            userInfos.find { it.groupId == group.id }?.joinDate
        }
    }.onEach { newGroups ->
            selectedGroup?.let { nonNullGroup ->
                selectedGroup = newGroups.find { group ->
                    group.id == nonNullGroup.id
                }
            } ?: run {
                selectedGroup = newGroups.getOrNull(0)
            }
        }.asInitialEmptyState()

    private val _userInfosFlow = apiServer.getAllUserInfosAsFlow()
    val userInfosFlow: StateFlow<List<UserInfo>> = _userInfosFlow.asState()

    fun selectGroup(group: Group) {
        selectedGroup = group
    }
}