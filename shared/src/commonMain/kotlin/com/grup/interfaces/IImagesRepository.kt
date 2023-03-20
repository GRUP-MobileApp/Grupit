package com.grup.interfaces

import com.grup.models.User

internal interface IImagesRepository {
    suspend fun uploadProfilePicture(user: User, pfp: ByteArray): String
}