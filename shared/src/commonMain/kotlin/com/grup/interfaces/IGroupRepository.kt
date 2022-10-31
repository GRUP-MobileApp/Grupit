package com.grup.interfaces

import com.grup.models.Group

internal interface IGroupRepository : IRepository {
    fun createGroup(group: Group): Group?

    fun findGroupById(groupId: String): Group?
}