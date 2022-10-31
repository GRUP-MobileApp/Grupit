package com.grup.controllers

import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.models.Group
import com.grup.service.GroupService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GroupController : KoinComponent {
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
}