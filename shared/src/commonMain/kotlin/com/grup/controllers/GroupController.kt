package com.grup.controllers

import com.grup.exceptions.MissingFieldException
import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.other.Id
import com.grup.service.GroupService
import com.grup.service.GroupInviteService
import com.grup.service.UserInfoService
import com.grup.service.UserService
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object GroupController : KoinComponent {
    private val userService: UserService by inject()
    private val groupService: GroupService by inject()
    private val userInfoService: UserInfoService by inject()
    private val groupInviteService: GroupInviteService by inject()

    fun createGroup(groupName: String, activeUser: User? = null): Group {
        val group = Group().apply {
            this.groupName = groupName
        }

        activeUser?.let { creator ->
            // Although Group object doesn't exist in Realm yet, this must be called first to
            // update Realm sync subscriptions so that createGroup creates a group that still lies
            // within bounds of realm sync subscriptions
            addUserToGroup(creator, group._id)
        }.run {
            return groupService.createGroup(group)
                ?: throw NotCreatedException("Error creating group $groupName")
        }
    }

    fun getGroupById(groupId: Id): Group {
        return groupService.getByGroupId(groupId)
            ?: throw NotFoundException("Group with id $groupId not found")
    }

    fun getAllGroupsAsFlow(): Flow<List<Group>> {
        return groupService.getAllGroupsAsFlow()
    }

    fun inviteUserToGroup(username: String, group: Group, activeUser: User? = null) {
        userService.getUserByUsername(username)?.let { foundUser ->
            groupInviteService.createGroupInvite(activeUser, foundUser, group)
        } ?: throw NotFoundException("User with username $username not found")
    }

    fun acceptInviteToGroup(groupInvite: GroupInvite, user: User) {
        groupInvite.groupId?.let { groupId ->
            addUserToGroup(user, groupId)
        } ?: throw MissingFieldException("Group invite missing groupId")
        groupInviteService.acceptGroupInvite(groupInvite)
    }

    private fun addUserToGroup(user: User, groupId: Id) {
        userInfoService.createUserInfo(UserInfo().apply {
            this.userId = user._id
            this.groupId = groupId
            this.nickname = user.username
        })
    }
}
