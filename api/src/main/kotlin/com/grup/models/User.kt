package com.grup.models

import kotlinx.serialization.Serializable

@Serializable
class User(
    var username: String
): BaseEntity()
