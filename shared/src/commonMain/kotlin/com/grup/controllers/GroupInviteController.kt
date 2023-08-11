package com.grup.controllers

import com.grup.exceptions.EntityAlreadyExistsException
import com.grup.exceptions.NotFoundException
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.service.GroupInviteService
import com.grup.service.UserInfoService
import com.grup.service.UserService
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupInviteController : KoinComponent {
    private val userService: UserService by inject()
    private val userInfoService: UserInfoService by inject()
    private val groupInviteService: GroupInviteService by inject()

    suspend fun createGroupInvite(
        inviter: User,
        inviteeUsername: String,
        group: Group
    ): GroupInvite {
        val foundInvitee: User = userService.getUserByUsername(inviteeUsername)
            ?: throw NotFoundException("User with username $inviteeUsername not found")
        val groupUserInfos: List<UserInfo> = userInfoService.findUserInfosByGroupId(group.id)

        if (groupUserInfos.any { it.user.id == foundInvitee.id }) {
            throw EntityAlreadyExistsException("$inviteeUsername is already in Group " +
                    group.groupName
            )
        }
        return groupInviteService.createGroupInvite(inviter, foundInvitee, group)
    }

    fun getAllGroupInvitesAsFlow(): Flow<List<GroupInvite>> {
        return groupInviteService.getAllGroupInvitesAsFlow()
    }

    suspend fun acceptGroupInvite(groupInvite: GroupInvite, user: User) {
        userInfoService.createUserInfo(user, groupInvite.groupId)
        groupInviteService.deleteGroupInvite(groupInvite)
    }

    suspend fun rejectGroupInvite(groupInvite: GroupInvite) {
        groupInviteService.deleteGroupInvite(groupInvite)
    }
}