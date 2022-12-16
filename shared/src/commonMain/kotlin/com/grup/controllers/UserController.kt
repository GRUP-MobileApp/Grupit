package com.grup.controllers

import com.grup.exceptions.NotFoundException
import com.grup.models.User
import com.grup.service.UserService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserController : KoinComponent {
    private val userService: UserService by inject()

    fun getUserByUsername(username: String): User {
        return userService.getUserByUsername(username)
            ?: throw NotFoundException("User with username $username not found")
    }
}
