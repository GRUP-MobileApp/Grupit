package com.grup.interfaces

import com.grup.models.Group
import com.grup.other.Id
import kotlinx.coroutines.flow.Flow

internal interface IGroupRepository : IRepository {
    fun createGroup(group: Group): Group?

    fun findGroupById(groupId: Id): Group?
    fun findAllGroupsAsFlow(): Flow<List<Group>>

    fun updateGroup(group: Group, block: (Group) -> Unit): Group?
}