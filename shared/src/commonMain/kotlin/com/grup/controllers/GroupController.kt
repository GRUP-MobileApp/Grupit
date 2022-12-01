package com.grup.controllers

import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.models.Group
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.other.Id
import com.grup.service.GroupService
import com.grup.service.UserInfoService
import com.grup.service.UserService
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GroupController : KoinComponent {
    private val userService: UserService by inject()
    private val groupService: GroupService by inject()
    private val userInfoService: UserInfoService by inject()

    fun createGroup(groupName: String, user: User? = null): Group {
        val group = Group().apply {
            this.groupName = groupName
        }

        return groupService.createGroup(group)?.also { createdGroup ->
            user?.let { creator ->
                addUserToGroup(creator, createdGroup)
            }
        } ?: throw NotCreatedException("Error creating group $groupName")
    }

    fun getGroupById(groupId: Id): Group {
        return groupService.getByGroupId(groupId)
            ?: throw NotFoundException("Group with id $groupId not found")
    }

    fun getAllGroupsAsFlow(): Flow<List<Group>> {
        return groupService.getAllGroupsAsFlow()
    }

    fun addUserToGroup(user: User, group: Group) {
//        val user: User = userService.getUserByUsername(username)
//            ?: throw DoesNotExistException("User with username $username doesn't exist")

        userInfoService.createUserInfo(UserInfo().apply {
            this.userId = user._id
            this.groupId = group._id
            this.nickname = user.username
        })
    }
}