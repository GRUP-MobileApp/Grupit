package com.grup.models

import kotlinx.serialization.Serializable

@Serializable
class Group(
    var groupName: String
): BaseEntity()
