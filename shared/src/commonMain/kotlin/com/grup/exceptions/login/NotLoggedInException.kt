package com.grup.exceptions.login

internal class NotLoggedInException(
    override val message: String? = "No logged in user"
) : LoginException(message)
