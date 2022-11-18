package com.grup.interfaces

import com.grup.models.User

internal interface IUserRepository : IRepository {
    fun insertUser(user: User): User?

    fun findUserById(userId: String): User?
    fun findUserByUserName(username: String): User?

    fun updateUser(user: User): User?
}