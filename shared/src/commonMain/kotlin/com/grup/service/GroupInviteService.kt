package com.grup.service

import com.grup.interfaces.IGroupInviteRepository
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.User
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupInviteService: KoinComponent {
    private val groupInviteRepository: IGroupInviteRepository by inject()

    fun createGroupInvite(inviter: User, invitee: User, group: Group): GroupInvite? {
        return groupInviteRepository.createGroupInvite(
            GroupInvite().apply {
                this.inviter = inviter.getId()
                this.inviterUsername = inviter.username!!
                this.invitee = invitee.getId()
                this.inviteeUsername = invitee.username!!
                this.groupId = group.getId()
                this.groupName = group.groupName!!
            }
        )
    }

    fun acceptGroupInvite(groupInvite: GroupInvite) {
        groupInviteRepository.updateGroupInviteStatus(groupInvite,
            Clock.System.now().toString())
    }

    fun getAllGroupInvitesAsFlow() = groupInviteRepository.findAllGroupInvitesAsFlow()
}