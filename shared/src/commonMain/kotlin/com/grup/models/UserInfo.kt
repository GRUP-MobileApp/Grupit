package com.grup.models

abstract class UserInfo : BaseEntity() {
    abstract val user: User
    abstract val groupId: String
    abstract var userBalance: Double
    abstract val joinDate: String
    abstract var latestViewDate: String
}
