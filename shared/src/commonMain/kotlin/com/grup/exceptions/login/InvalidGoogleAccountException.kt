package com.grup.exceptions.login

class InvalidGoogleAccountException(
    override val message: String? = "Invalid Google Account"
) : LoginException(message)