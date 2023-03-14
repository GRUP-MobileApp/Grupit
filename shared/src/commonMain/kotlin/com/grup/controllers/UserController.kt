package com.grup.controllers

import com.grup.models.User
import com.grup.service.UserService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserController : KoinComponent {
    private val userService: UserService by inject()

    suspend fun getUserById(userId: String): User? {
        return userService.getUserById(userId)
    }

    private suspend fun getUserByUsername(username: String): User? {
        return userService.getUserByUsername(username)
    }

    suspend fun usernameExists(username: String): Boolean {
        return getUserByUsername(username) != null
    }
}
