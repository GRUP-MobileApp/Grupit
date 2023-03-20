package com.grup.service

import com.grup.exceptions.EmptyArgumentException
import com.grup.interfaces.IImagesRepository
import com.grup.models.User
import com.grup.interfaces.IUserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserService : KoinComponent {
    private val userRepository: IUserRepository by inject()
    private val imagesRepository: IImagesRepository by inject()

    suspend fun createMyUser(
        username: String,
        displayName: String,
        profilePicture: ByteArray
    ): User? {
        return userRepository.createMyUser(username, displayName)?.also { user ->
            if (profilePicture.isNotEmpty()) {
                imagesRepository.uploadProfilePicture(user, profilePicture)
                    .let { profilePictureURL ->
                        userRepository.updateUser(user) {
//                            this.profilePictureURL = profilePictureURL
                        }
                    }
            }
        }
    }

    fun getMyUser(): User? {
        return userRepository.findMyUser()
    }
    suspend fun getUserById(userId: String): User? {
        return userRepository.findUserById(userId)
    }

    suspend fun getUserByUsername(username: String): User? {
        if (username.isBlank()) {
            throw EmptyArgumentException("Please enter a username")
        }
        return userRepository.findUserByUsername(username)
    }
}