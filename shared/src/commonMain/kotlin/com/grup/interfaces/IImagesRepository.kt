package com.grup.interfaces

internal interface IImagesRepository {
    suspend fun uploadProfilePicture(userId: String, pfp: ByteArray): String?

    suspend fun deleteProfilePicture(profilePictureURL: String)
}