package com.grup.exceptions.login

internal class InvalidGoogleAccountException(
    override val message: String? = "Invalid Google Account"
) : LoginException(message)