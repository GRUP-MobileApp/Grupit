package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.exceptions.APIException
import com.grup.models.Group
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

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

    fun selectGroup(group: Group) {
        selectedGroupMutable.value = group
    }
}