package com.grup.controllers

import com.grup.models.Group
import com.grup.models.User
import com.grup.service.GroupService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupController : KoinComponent {
    private val groupService: GroupService by inject()

    suspend fun createGroup(creator: User, groupName: String): Group {
        return groupService.createGroup(creator, groupName)
    }

    fun getAllGroupsAsFlow() = groupService.getAllGroupsAsFlow()
}
