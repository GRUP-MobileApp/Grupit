package com.grup.exceptions

internal class UserAlreadyInGroupException(
    override val message: String? = "User is already in Group"
) : APIException(message)