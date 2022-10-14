package com.grup.interfaces

import com.grup.models.User

interface IUserRepository {
    fun insertUser(user: User): User?

    fun findUserById(userId: String): User?
    fun findUserByUserName(username: String): User?
}