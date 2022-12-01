package com.grup.service

import com.grup.models.Group
import com.grup.interfaces.IGroupRepository
import com.grup.models.User
import com.grup.other.Id
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupService : KoinComponent {
    private val groupRepository: IGroupRepository by inject()

    fun createGroup(group: Group): Group? {
        return groupRepository.createGroup(group)
    }

    fun getByGroupId(groupId: Id): Group? {
        return groupRepository.findGroupById(groupId)
    }

    fun getAllGroupsAsFlow(): Flow<List<Group>> {
        return groupRepository.findAllGroupsAsFlow()
    }
}