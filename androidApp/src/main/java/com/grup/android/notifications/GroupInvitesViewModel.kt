package com.grup.android.notifications

import com.grup.android.LoggedInViewModel
import com.grup.models.GroupInvite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class GroupInvitesViewModel : LoggedInViewModel() {
    companion object {
        var groupInvitesAmount: MutableStateFlow<Int> = MutableStateFlow(0)
    }

    private val _groupInvitesFlow = apiServer.getAllGroupInvitesAsFlow()
    val groupInvites: StateFlow<List<GroupInvite>> =
        _groupInvitesFlow.map { groupInvites ->
            groupInvites.sortedByDescending { notification ->
                notification.date
            }.also { sortedGroupInvites ->
                groupInvitesAmount.value = sortedGroupInvites.size
            }
        }.asNotification(emptyList())
}