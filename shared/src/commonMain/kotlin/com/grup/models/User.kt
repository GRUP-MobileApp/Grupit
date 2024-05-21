package com.grup.models

import kotlinx.datetime.Instant

abstract class User internal constructor(): BaseEntity() {
    object DeletedUser: User() {
        override val username: String
            get() = "Deleted User"
        override var displayName: String
            get() = "Deleted Name"
            set(value) { }
        override var venmoUsername: String
            get() = "Deleted"
            set(_) { }
        override var profilePictureURL: String?
            get() = null
            set(_) { }
        override var latestViewDate: Instant
            get() = Instant.DISTANT_PAST
            set(_) { }
        override var _id: String
            get() = ""
            set(_) { }

    }
    abstract val username: String
    abstract var displayName: String
    abstract var venmoUsername: String
    abstract var profilePictureURL: String?

    abstract var latestViewDate: Instant
}
