package com.grup.service

import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.dbmanager.DatabaseManager
import com.grup.interfaces.IGroupInviteRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.models.GroupInvite
import com.grup.models.User
import com.grup.models.UserInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupInviteService(private val dbManager: DatabaseManager): KoinComponent {
    private val userRepository: IUserRepository by inject()
    private val userInfoRepository: IUserInfoRepository by inject()
    private val groupInviteRepository: IGroupInviteRepository by inject()

    suspend fun createGroupInvite(inviterUserInfo: UserInfo, inviteeUsername: String): GroupInvite {
        val invitee: User = userRepository.findUserByUsername(inviteeUsername)
            ?: throw NotFoundException("User with username $inviteeUsername not found")
        return dbManager.write {
            groupInviteRepository.createGroupInvite(this, inviterUserInfo, invitee)
                ?: throw NotCreatedException("Error creating GroupInvite to ${invitee.displayName}")
        }
    }

    // TODO: Create accept and rejectGroupInvite

    suspend fun acceptGroupInvite(groupInvite: GroupInvite, user: User) = dbManager.write {
        userInfoRepository.createUserInfo(this, user, groupInvite.group)
        groupInviteRepository.deleteGroupInvite(this, groupInvite)
    }

    suspend fun rejectGroupInvite(groupInvite: GroupInvite) = dbManager.write {
        groupInviteRepository.deleteGroupInvite(this, groupInvite)
    }

    fun getAllGroupInvitesAsFlow() = groupInviteRepository.findAllGroupInvitesAsFlow()
}