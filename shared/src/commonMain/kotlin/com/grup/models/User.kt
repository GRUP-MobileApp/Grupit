package com.grup.models

import kotlinx.datetime.Instant

abstract class User internal constructor(): BaseEntity() {
    abstract val username: String
    abstract var displayName: String
    abstract var venmoUsername: String
    abstract var profilePictureURL: String

    abstract var latestViewDate: Instant
}
