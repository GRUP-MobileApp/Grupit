package com.grup.exceptions.login

internal class InvalidGoogleAccountException(
    override val message: String? = "Google account error"
) : LoginException(message)