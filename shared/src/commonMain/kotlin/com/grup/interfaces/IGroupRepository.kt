package com.grup.interfaces

import com.grup.models.Group
import com.grup.models.User
import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction

internal interface IGroupRepository : IRepository {
    fun createGroup(
        transaction: DatabaseWriteTransaction,
        user: User, groupName: String
    ): Group?

    fun findGroupById(groupId: String): Group?

    fun updateGroup(
        transaction: DatabaseWriteTransaction,
        group: Group,
        block: Group.() -> Unit
    ): Group?
}