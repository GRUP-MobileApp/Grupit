package com.grup.controllers

import com.grup.exceptions.NotCreatedException
import com.grup.models.Group
import com.grup.models.User
import com.grup.service.GroupService
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object GroupController : KoinComponent {
    private val groupService: GroupService by inject()
    private val userInfoService: UserInfoService by inject()

    fun createGroup(creator: User, groupName: String): Group {
        val group = Group().apply {
            this.groupName = groupName
        }

        userInfoService.createUserInfo(creator, group.getId())
        return groupService.createGroup(group)
            ?: throw NotCreatedException("Error creating group $groupName")
    }

    fun getAllGroupsAsFlow() = groupService.getAllGroupsAsFlow()
}
