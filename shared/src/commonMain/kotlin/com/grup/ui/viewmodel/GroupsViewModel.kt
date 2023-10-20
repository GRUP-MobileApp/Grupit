package com.grup.ui.viewmodel

import com.grup.models.Group
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach

internal class GroupsViewModel : LoggedInViewModel() {
    private val _groupsFlow = apiServer.getAllGroupsAsFlow()
    val groups: StateFlow<List<Group>> = _groupsFlow.onEach { newGroups ->
        selectedGroupMutable.value?.let { nonNullGroup ->
            selectedGroupMutable.value = newGroups.find { group ->
                group.id == nonNullGroup.id
            }
        } ?: run {
            selectedGroupMutable.value = newGroups.getOrNull(0)
        }
    }.asState()

    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    val myUserInfosFlow: StateFlow<List<UserInfo>> = _myUserInfosFlow.asState()

    fun selectGroup(group: Group) {
        selectedGroupMutable.value = group
    }
}