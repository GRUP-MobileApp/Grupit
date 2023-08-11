package com.grup.service

import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.IGroupInviteRepository
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupInviteService: KoinComponent {
    private val groupInviteRepository: IGroupInviteRepository by inject()

    fun createGroupInvite(inviter: User, invitee: User, group: Group): GroupInvite {
        return groupInviteRepository.createGroupInvite(inviter, invitee, group)
            ?: throw NotCreatedException("Error creating group invite to ${invitee.displayName}")
    }

    suspend fun deleteGroupInvite(groupInvite: GroupInvite) {
        groupInviteRepository.deleteGroupInvite(groupInvite)
    }

    fun getAllGroupInvitesAsFlow() = groupInviteRepository.findAllGroupInvitesAsFlow()
}