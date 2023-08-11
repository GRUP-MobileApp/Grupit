package com.grup.models

import kotlinx.serialization.Serializable

abstract class User : BaseEntity() {
    abstract val username: String
    abstract var displayName: String
    abstract var profilePictureURL: String
}
