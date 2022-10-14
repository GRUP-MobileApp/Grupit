package com.grup.interfaces

import com.grup.models.Group

interface IGroupRepository {
    fun createGroup(group: Group): Group?

    fun findGroupById(groupId: String): Group?
}