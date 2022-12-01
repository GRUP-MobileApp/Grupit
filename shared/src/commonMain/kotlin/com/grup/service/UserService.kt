package com.grup.service

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import com.grup.other.Id
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserService : KoinComponent {
    private val userRepository: IUserRepository by inject()

    fun createUser(user: User): User? {
        return userRepository.insertUser(user)
    }

    fun getUserByUsername(username: String): User? {
        return userRepository.findUserByUserName(username)
    }

    fun getUserById(userId: Id): User? {
        return userRepository.findUserById(userId)
    }

    fun usernameExists(username: String): Boolean {
        return getUserByUsername(username) != null
    }
}