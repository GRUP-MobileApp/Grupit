package com.grup.android.notifications

import androidx.lifecycle.viewModelScope
import com.grup.android.LoggedInViewModel
import com.grup.models.GroupInvite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class GroupInvitesViewModel : LoggedInViewModel() {
    companion object {
        var groupInvitesAmount: MutableStateFlow<Int> = MutableStateFlow(0)
    }

    private val _groupInvitesFlow = apiServer.getAllGroupInvitesAsFlow()
    val groupInvites: StateFlow<List<GroupInvite>> =
        _groupInvitesFlow.map { groupInvites ->
            groupInvites.filter { groupInvite ->
                groupInvite.invitee!! == apiServer.user.getId() &&
                        groupInvite.dateAccepted == GroupInvite.PENDING
            }.sortedByDescending { groupInvite ->
                groupInvite.date
            }.also { sortedGroupInvites ->
                groupInvitesAmount.value = sortedGroupInvites.size
            }
        }.asNotification(emptyList())

    fun acceptGroupInvite(groupInvite: GroupInvite) = viewModelScope.launch {
        apiServer.acceptGroupInvite(groupInvite)
    }
    fun rejectGroupInvite(groupInvite: GroupInvite) = viewModelScope.launch {
        apiServer.rejectGroupInvite(groupInvite)
    }
}