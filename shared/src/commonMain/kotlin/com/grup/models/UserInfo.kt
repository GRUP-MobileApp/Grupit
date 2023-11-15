package com.grup.models

abstract class UserInfo : BaseEntity() {
    abstract val user: User
    abstract val group: Group
    abstract var userBalance: Double
    abstract val joinDate: String
}
