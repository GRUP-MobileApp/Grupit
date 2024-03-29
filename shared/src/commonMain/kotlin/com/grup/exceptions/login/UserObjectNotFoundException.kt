package com.grup.exceptions.login

internal class UserObjectNotFoundException(
    override val message: String? = "User object not created yet"
) : LoginException(message)
