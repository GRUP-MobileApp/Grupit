package com.grup.service

import com.grup.exceptions.UserAlreadyInGroupException
import com.grup.models.User
import com.grup.interfaces.IUserRepository
import com.grup.models.Group
import com.grup.objects.throwIf
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

    fun getUserById(userId: String): User? {
        return userRepository.findUserById(userId)
    }

    fun addGroupToUser(user: User, group: Group) {
        throwIf(user.groups.contains(group.getId())) {
            UserAlreadyInGroupException("User with id ${user.getId()} is already in " +
                    "Group with id ${group.getId()}")
        }
        user.apply {
            groups.add(group.getId())
        }
        userRepository.updateUser(user)
    }

    fun usernameExists(username: String): Boolean {
        return getUserByUsername(username) != null
    }
}