package com.grup.interfaces

import com.grup.models.User
import com.grup.other.Id

internal interface IUserRepository : IRepository {
    fun insertUser(user: User): User?

    fun findUserById(userId: Id): User?
    fun findUserByUserName(username: String): User?

    fun updateUser(user: User): User?
}