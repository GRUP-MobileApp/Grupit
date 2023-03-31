package com.grup.controllers

import com.grup.models.User
import com.grup.service.UserService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserController : KoinComponent {
    private val userService: UserService by inject()

    suspend fun createUser(
        username: String,
        displayName: String,
        profilePicture: ByteArray
    ): User? {
        return userService.createMyUser(username, displayName, profilePicture)
    }

    fun getMyUser(): User? {
        return userService.getMyUser()
    }

    suspend fun usernameExists(username: String): Boolean {
        return userService.getUserByUsername(username) != null
    }
}
