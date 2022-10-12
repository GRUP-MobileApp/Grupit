package com.grup.service

import com.grup.models.Group
import com.mongodb.client.MongoClient
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.*
import org.litote.kmongo.id.toId

class GroupService : KoinComponent {
    private val client: MongoClient by inject()
    private val database = client.getDatabase("group")
    private val groupCollection = database.getCollection<Group>()

    fun createGroup(group: Group): Id<Group>? {
        groupCollection.insertOne(group)
        return group.id
    }

    fun getByGroupId(groupId: String): Group? {
        val groupBsonId: Id<Group> = ObjectId(groupId).toId()

        return groupCollection.findOne { Group::id eq groupBsonId }
    }

    fun groupIdExists(groupId: String): Boolean {
        return getByGroupId(groupId) != null
    }
}