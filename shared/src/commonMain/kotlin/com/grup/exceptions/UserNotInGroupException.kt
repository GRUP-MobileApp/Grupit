package com.grup.exceptions

class UserNotInGroupException(
    override val message: String? = "User not in Group"
) : APIException(message)