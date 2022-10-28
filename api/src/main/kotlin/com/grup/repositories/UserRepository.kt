package com.grup.repositories

import com.grup.models.User
import com.grup.interfaces.IUserRepository

internal class UserRepository : IUserRepository {
    override fun insertUser(user: User): User? {
        TODO("Not yet implemented")
    }

    override fun findUserById(userId: String): User? {
        TODO("Not yet implemented")
    }

    override fun findUserByUserName(username: String): User? {
        TODO("Not yet implemented")
    }
}