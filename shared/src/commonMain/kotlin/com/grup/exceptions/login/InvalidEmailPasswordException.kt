package com.grup.exceptions.login

class InvalidEmailPasswordException(
    override val message: String? = "Invalid email/password"
) : LoginException(message)