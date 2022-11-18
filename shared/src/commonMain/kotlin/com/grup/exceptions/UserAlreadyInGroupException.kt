package com.grup.exceptions

class UserAlreadyInGroupException(
    override val message: String? = "User is already in Group"
) : Exception(message)