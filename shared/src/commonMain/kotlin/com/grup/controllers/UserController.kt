package com.grup.controllers

import com.grup.models.User
import com.grup.service.UserService
import com.grup.service.ValidationService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserController : KoinComponent {
    private val userService: UserService by inject()
    private val validationService: ValidationService by inject()

    suspend fun createUser(
        username: String,
        displayName: String,
        venmoUsername: String?,
        profilePicture: ByteArray,
    ): User {
        validationService.validateUsername(username)
        validationService.validateNickname(displayName)
        return userService.createMyUser(username, displayName, venmoUsername, profilePicture)
    }

    fun getMyUser(): User? {
        return userService.getMyUser()
    }

    suspend fun usernameExists(username: String): Boolean {
        return userService.getUserByUsername(username) != null
    }

    suspend fun updateLatestTime(user: User) {
        userService.updateLatestTime(user)
    }
}
