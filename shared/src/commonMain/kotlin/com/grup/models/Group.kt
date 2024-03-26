package com.grup.models

abstract class Group internal constructor() : BaseEntity() {
    abstract var groupName: String
}
