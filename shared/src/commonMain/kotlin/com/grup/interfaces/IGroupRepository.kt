package com.grup.interfaces

import com.grup.models.Group
import com.grup.models.User
import kotlinx.coroutines.flow.Flow

internal interface IGroupRepository : IRepository {
    suspend fun createGroup(user: User, groupName: String): Group?

    fun findGroupById(groupId: String): Group?
    fun findAllGroupsAsFlow(): Flow<List<Group>>

    suspend fun updateGroup(group: Group, block: Group.() -> Unit): Group?
}