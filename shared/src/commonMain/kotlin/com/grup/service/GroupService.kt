package com.grup.service

import com.grup.exceptions.NotCreatedException
import com.grup.models.Group
import com.grup.interfaces.IGroupRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupService : KoinComponent {
    private val groupRepository: IGroupRepository by inject()

    suspend fun createGroup(group: Group): Group {
        return groupRepository.createGroup(group)
            ?: throw NotCreatedException("Error creating group ${group.groupName}")
    }

    fun getAllGroupsAsFlow() = groupRepository.findAllGroupsAsFlow()
}