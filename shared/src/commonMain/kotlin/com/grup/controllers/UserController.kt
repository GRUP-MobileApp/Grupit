package com.grup.controllers

import com.grup.exceptions.EntityAlreadyExistsException
import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.models.User
import com.grup.service.UserService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserController : KoinComponent {
    private val userService: UserService by inject()

    // Don't need this
    fun createUser(username: String): User {
        if (userService.usernameExists(username)) {
            throw EntityAlreadyExistsException("User with username $username already exists")
        }

        val user = User().apply {
            this.username = username
        }
        return userService.createUser(user)
            ?: throw NotCreatedException("Error creating User with username $username")
    }

    fun getUserByUsername(username: String): User {
        return userService.getUserByUsername(username)
            ?: throw NotFoundException("User with username $username not found")
    }
}
