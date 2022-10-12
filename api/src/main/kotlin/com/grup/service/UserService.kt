package com.grup.service

import com.grup.models.User
import com.mongodb.client.MongoClient
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.*
import org.litote.kmongo.id.toId

class UserService : KoinComponent {
    private val client: MongoClient by inject()
    private val database = client.getDatabase("user")
    private val userCollection = database.getCollection<User>()

    fun createUser(user: User): Id<User>? {
        userCollection.insertOne(user)
        return user.id
    }

    fun getByUsername(username: String): User? {
        return userCollection.findOne { User::username eq username }
    }

    fun getUserById(userId: String): User? {
        val userBsonId: Id<User> = ObjectId(userId).toId()

        return userCollection.findOne { User::id eq userBsonId }
    }

    fun usernameExists(username: String): Boolean {
        return getByUsername(username) != null
    }

    fun userIdExists(userId: String): Boolean {
        return getUserById(userId) != null
    }
}