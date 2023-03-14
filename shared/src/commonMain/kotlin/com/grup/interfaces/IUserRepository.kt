package com.grup.interfaces

import com.grup.models.User

internal interface IUserRepository : IRepository {
    suspend fun findUserById(userId: String): User?
    suspend fun findUserByUsername(username: String): User?
}