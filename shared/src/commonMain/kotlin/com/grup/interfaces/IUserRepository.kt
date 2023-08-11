package com.grup.interfaces

import com.grup.models.User

internal interface IUserRepository : IRepository {
    suspend fun createMyUser(
        username: String,
        displayName: String
    ): User?

    fun findMyUser(): User?
    suspend fun findUserByUsername(username: String): User?

    suspend fun updateUser(user: User, block: User.() -> Unit): User
}