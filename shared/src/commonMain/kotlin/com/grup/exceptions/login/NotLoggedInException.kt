package com.grup.exceptions.login

class NotLoggedInException(
    override val message: String? = "No logged in user"
) : LoginException(message)