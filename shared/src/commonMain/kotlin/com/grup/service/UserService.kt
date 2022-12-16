package com.grup.service

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserService : KoinComponent {
    private val userRepository: IUserRepository by inject()

    fun getUserByUsername(username: String): User? {
        return userRepository.findUserByUserName(username)
    }

    fun usernameExists(username: String): Boolean {
        return getUserByUsername(username) != null
    }
}