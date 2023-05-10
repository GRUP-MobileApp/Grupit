package com.grup.exceptions

internal class UserNotInGroupException(
    override val message: String? = "User not in Group"
) : APIException(message)