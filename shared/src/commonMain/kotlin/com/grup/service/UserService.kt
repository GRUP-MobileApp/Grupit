package com.grup.service

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserService : KoinComponent {
    private val userRepository: IUserRepository by inject()

    fun getUserById(userId: String): User? {
        return userRepository.findUserById(userId)
    }

    fun getUserByUsername(username: String): User? {
        return userRepository.findUserByUsername(username)
    }
}