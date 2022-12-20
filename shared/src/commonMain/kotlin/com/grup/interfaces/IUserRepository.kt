package com.grup.interfaces

import com.grup.models.User

internal interface IUserRepository : IRepository {
    fun findUserById(realmUserId: String): User?
    fun findUserByUserName(username: String): User?
}