package com.grup.service

import com.grup.interfaces.IGroupInviteRepository
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.User
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupInviteService: KoinComponent {
    private val groupInviteRepository: IGroupInviteRepository by inject()

    fun createGroupInvite(inviter: User?, invitee: User, group: Group): GroupInvite? {
        return groupInviteRepository.createGroupInvite(
            GroupInvite().apply {
                this.inviter = inviter?._id
                this.invitee = invitee._id
                this.groupId = group._id
                this.groupName = group.groupName
            }
        )
    }

    fun acceptGroupInvite(groupInvite: GroupInvite) {
        groupInviteRepository.updateGroupInviteStatus(groupInvite,
            GroupInvite.RequestStatus.ACCEPTED)
    }

    fun rejectGroupInvite(groupInvite: GroupInvite) {
        groupInviteRepository.updateGroupInviteStatus(groupInvite,
            GroupInvite.RequestStatus.REJECTED)
    }

    fun getAllGroupInvitesAsFlow(): Flow<List<GroupInvite>> {
        return groupInviteRepository.findAllGroupInvitesAsFlow()
    }
}