package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.models.GroupInvite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class GroupInvitesViewModel : LoggedInViewModel() {
    private val _groupInvitesFlow = apiServer.getAllGroupInvitesAsFlow()
    val groupInvites: StateFlow<List<GroupInvite>> =
        _groupInvitesFlow.map { groupInvites ->
            groupInvites.filter { groupInvite ->
                groupInvite.inviteeId == apiServer.user.id &&
                        groupInvite.dateAccepted == GroupInvite.PENDING
            }.sortedByDescending { groupInvite ->
                groupInvite.date
            }.also { sortedGroupInvites ->
                groupInvitesCount.value = sortedGroupInvites.size
            }
        }.asNotification(emptyList())


    val groupInvitesCount: MutableStateFlow<Int> = MutableStateFlow(0)

    fun acceptGroupInvite(groupInvite: GroupInvite) = coroutineScope.launch {
        apiServer.acceptGroupInvite(groupInvite)
    }
    fun rejectGroupInvite(groupInvite: GroupInvite) = coroutineScope.launch {
        apiServer.rejectGroupInvite(groupInvite)
    }
}