package com.grup.controllers

import com.grup.models.User
import com.grup.service.UserService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserController : KoinComponent {
    private val userService: UserService by inject()

    private fun getUserByUsername(username: String): User? {
        return userService.getUserByUsername(username)
    }

    fun usernameExists(username: String): Boolean {
        return getUserByUsername(username) != null
    }
}
