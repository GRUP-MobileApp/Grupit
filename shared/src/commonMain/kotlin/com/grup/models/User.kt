package com.grup.models

abstract class User : BaseEntity() {
    abstract val username: String
    abstract var displayName: String
    abstract var venmoUsername: String?
    abstract var profilePictureURL: String

    abstract var latestViewDate: String
}
