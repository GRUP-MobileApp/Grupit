package com.grup.controllers

import com.grup.exceptions.DoesNotExistException
import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.models.Group
import com.grup.models.User
import com.grup.objects.Id
import com.grup.service.GroupService
import com.grup.service.UserService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GroupController : KoinComponent {
    private val userService: UserService by inject()
    private val groupService: GroupService by inject()

    fun createGroup(groupName: String): Group {
        val group = Group().apply {
            this.groupName = groupName
        }

        return groupService.createGroup(group)
            ?: throw NotCreatedException("Error creating group $groupName")
    }

    fun getGroupById(groupId: String): Group {
        return groupService.getByGroupId(groupId)
            ?: throw NotFoundException("Group with id $groupId not found")
    }

    fun getUsersByGroupId(groupId: String): List<Id> {
        val group: Group = groupService.getByGroupId(groupId)
            ?: throw DoesNotExistException("Group with id $groupId doesn't exist")
        return group.userInfo.map { userInfo -> userInfo.userId!! }
    }

    fun addUserToGroup(group: Group, username: String) {
        val user: User = userService.getUserByUsername(username)
            ?: throw DoesNotExistException("User with username $username doesn't exist")

        userService.addGroupToUser(user, group)
        // Can't rollback if inconsistency user.groups and group.userInfo
        groupService.addUserToGroup(user, group)
    }
}