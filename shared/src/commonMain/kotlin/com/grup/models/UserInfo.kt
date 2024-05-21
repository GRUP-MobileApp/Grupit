package com.grup.models

import kotlinx.datetime.Instant

abstract class UserInfo internal constructor(): BaseEntity() {
    abstract val user: User
    abstract val group: Group
    abstract var userBalance: Double
    abstract val joinDate: Instant

    abstract val isActive: Boolean

    abstract fun invalidateUserInfo(removeUser: Boolean = false)
}
