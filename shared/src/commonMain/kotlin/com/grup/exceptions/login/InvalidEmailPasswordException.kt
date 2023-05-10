package com.grup.exceptions.login

internal class InvalidEmailPasswordException(
    override val message: String? = "Invalid email/password"
) : LoginException(message)