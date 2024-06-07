package com.grup.service

import AlreadyExistsException
import com.grup.dbmanager.DatabaseManager
import com.grup.exceptions.MaximumObjectsException
import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.exceptions.UserAlreadyInGroupException
import com.grup.interfaces.IGroupInviteRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.models.GroupInvite
import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupInviteService(private val dbManager: DatabaseManager): KoinComponent {
    private val userRepository: IUserRepository by inject()
    private val userInfoRepository: IUserInfoRepository by inject()
    private val groupInviteRepository: IGroupInviteRepository by inject()

    suspend fun createGroupInvite(inviterUserInfo: UserInfo, inviteeUsername: String): GroupInvite {
        val invitee: User = userRepository.findUserByUsername(inviteeUsername)
            ?: throw NotFoundException("User with username $inviteeUsername not found")

        val groupUsers: List<UserInfo> =
            userInfoRepository.findAllUserInfosAsFlow().first().filter {
                it.group.id == inviterUserInfo.group.id
            }

        if (groupUsers.any { it.user.id == invitee.id }) {
            throw UserAlreadyInGroupException()
        }

        if (
            getAllGroupInvitesAsFlow().first().any {
                it.inviter.id == inviterUserInfo.user.id && it.group.id == inviterUserInfo.group.id
            }
        ) {
            throw AlreadyExistsException("You have already invited this user")
        }

        return dbManager.write {
            groupInviteRepository.createGroupInvite(this, inviterUserInfo, invitee)
                ?: throw NotCreatedException("Error creating GroupInvite to ${invitee.displayName}")
        }
    }

    suspend fun acceptGroupInvite(groupInvite: GroupInvite, user: User) {
        val myUserInfos: List<UserInfo> =
            userInfoRepository.findMyUserInfosAsFlow(false).first()
        if (myUserInfos.count { it.isActive } >= 5) {
            throw MaximumObjectsException("Cannot exceed 5 groups")
        }

        val existingUserInfo: UserInfo? = myUserInfos.find { userInfo ->
            userInfo.group.id == groupInvite.group.id
        }
        if (existingUserInfo?.isActive == true) {
            throw UserAlreadyInGroupException()
        }

        // Delete all invites to group targeting user regardless
        val groupInvites: List<GroupInvite> = getAllGroupInvitesAsFlow().first().filter {
            it.inviteeId == user.id && groupInvite.group.id == it.group.id
        }

        dbManager.write {
           if (existingUserInfo != null) {
                userInfoRepository.updateUserInfo(this, existingUserInfo) {
                    isActive = true
                }
            } else {
               userInfoRepository.createUserInfo(this, user, groupInvite.group)
           }
            groupInvites.forEach {
                groupInviteRepository.deleteGroupInvite(this, it)
            }
        }
    }

    suspend fun rejectGroupInvite(groupInvite: GroupInvite) = dbManager.write {
        groupInviteRepository.deleteGroupInvite(this, groupInvite)
    }

    fun getAllGroupInvitesAsFlow() = groupInviteRepository.findAllGroupInvitesAsFlow()
}