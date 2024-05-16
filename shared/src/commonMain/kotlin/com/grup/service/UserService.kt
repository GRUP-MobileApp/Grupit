package com.grup.service

import com.grup.dbmanager.DatabaseManager
import com.grup.exceptions.EmptyArgumentException
import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.IImagesRepository
import com.grup.interfaces.IUserRepository
import com.grup.models.User
import com.grup.other.getCurrentTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserService(private val dbManager: DatabaseManager) : KoinComponent {
    private val userRepository: IUserRepository by inject()
    private val imagesRepository: IImagesRepository by inject()

    private val validationService: ValidationService = ValidationService()

    suspend fun createMyUser(
        username: String,
        displayName: String,
        venmoUsername: String?,
        profilePicture: ByteArray
    ): User {
        return dbManager.write {
            userRepository.createMyUser(this, username, displayName, venmoUsername)
                ?: throw NotCreatedException("Error creating user object")
        }.also { user ->
            updateProfilePicture(user, profilePicture)
        }
    }

    fun getMyUser(): User? {
        return userRepository.findMyUser()
    }

    suspend fun getUserByUsername(username: String): User? {
        if (username.isBlank()) {
            throw EmptyArgumentException("Please enter a username")
        }
        return userRepository.findUserByUsername(username)
    }

    suspend fun updateUser(user: User, block: User.() -> Unit): User = dbManager.write {
        userRepository.updateUser(this, user) {
            apply(block)
            displayName.split(" ", limit = 2).let {
                validationService.validateName(it[0])
                if (it.size == 2) {
                    validationService.validateName(it[1])
                }
            }
            validationService.validateVenmoUsername(venmoUsername)
        }
    }

    suspend fun updateLatestTime(user: User) = updateUser(user) {
        latestViewDate = getCurrentTime()
    }

    suspend fun updateProfilePicture(user: User, profilePicture: ByteArray) {
        user.profilePictureURL?.let {
            imagesRepository.deleteProfilePicture(it)
        }

        imagesRepository.uploadProfilePicture(user.id, profilePicture).let { pfpURL ->
            dbManager.write {
                userRepository.updateUser(this, user) {
                    this.profilePictureURL = pfpURL
                }
            }
        }
    }
}