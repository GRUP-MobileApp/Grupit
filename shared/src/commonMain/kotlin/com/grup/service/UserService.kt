package com.grup.service

import com.grup.exceptions.EmptyArgumentException
import com.grup.models.User
import com.grup.interfaces.IUserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserService : KoinComponent {
    private val userRepository: IUserRepository by inject()

    suspend fun getUserById(userId: String) = userRepository.findUserById(userId)

    suspend fun getUserByUsername(username: String): User? {
        if (username.isBlank()) {
            throw EmptyArgumentException("Please enter a username")
        }
        return userRepository.findUserByUsername(username)
    }
}