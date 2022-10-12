package com.grup.models

import kotlinx.serialization.Serializable

@Serializable
abstract class BaseEntity {
    abstract fun getId(): String
}