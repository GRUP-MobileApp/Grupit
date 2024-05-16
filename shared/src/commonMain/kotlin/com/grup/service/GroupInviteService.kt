package com.grup.service

import com.grup.dbmanager.DatabaseManager
import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.exceptions.UserAlreadyInGroupException
import com.grup.interfaces.IGroupInviteRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupInviteService(private val dbManager: DatabaseManager): KoinComponent {
    private val userRepository: IUserRepository by inject()
    private val userInfoRepository: IUserInfoRepository by inject()
    private val groupInviteRepository: IGroupInviteRepository by inject()

    suspend fun createGroupInvite(inviterUserInfo: UserInfo, inviteeUsername: String): GroupInvite {
        val invitee: User = userRepository.findUserByUsername(inviteeUsername)
            ?: throw NotFoundException("User with username $inviteeUsername not found")

        if (isUserInGroup(invitee, inviterUserInfo.group)) {
            throw UserAlreadyInGroupException()
        }

        return dbManager.write {
            groupInviteRepository.createGroupInvite(this, inviterUserInfo, invitee)
                ?: throw NotCreatedException("Error creating GroupInvite to ${invitee.displayName}")
        }
    }

    suspend fun acceptGroupInvite(groupInvite: GroupInvite, user: User) {
        // Delete all invites to group targeting user
        val groupInvites = getAllGroupInvitesAsFlow().first().filter {
            it.inviteeId == user.id && groupInvite.group.id == it.group.id
        }

        // GroupInvites should be deleted either way
        if (isUserInGroup(user, groupInvite.group)) {
            throw UserAlreadyInGroupException()
        }

        return dbManager.write {
            userInfoRepository.createUserInfo(this, user, groupInvite.group)
            groupInvites.forEach {
                groupInviteRepository.deleteGroupInvite(this, it)
            }
        }
    }

    suspend fun rejectGroupInvite(groupInvite: GroupInvite) = dbManager.write {
        groupInviteRepository.deleteGroupInvite(this, groupInvite)
    }

    fun getAllGroupInvitesAsFlow() = groupInviteRepository.findAllGroupInvitesAsFlow()

    private suspend fun isUserInGroup(user: User, group: Group): Boolean {
        val groupUsers: List<User> = userInfoRepository.findAllUserInfosAsFlow().map { userInfos ->
            userInfos.filter { it.group.id == group.id }.map { it.user }
        }.first()

        return groupUsers.any { it.id == user.id }
    }
}