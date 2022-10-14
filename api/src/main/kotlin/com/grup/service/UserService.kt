package com.grup.service

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserService : KoinComponent {
    private val userRepository: IUserRepository by inject()

    fun createUser(user: User): User? {
        return userRepository.insertUser(user)
    }

    fun getUserByUsername(username: String): User? {
        return userRepository.findUserByUserName(username)
    }

    fun getUserById(userId: String): User? {
        return userRepository.findUserById(userId)
    }

    fun usernameExists(username: String): Boolean {
        return getUserByUsername(username) != null
    }

    fun userIdExists(userId: String): Boolean {
        return getUserById(userId) != null
    }
}