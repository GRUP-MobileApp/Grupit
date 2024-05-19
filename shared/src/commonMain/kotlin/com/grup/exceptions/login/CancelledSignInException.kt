package com.grup.exceptions.login

internal class CancelledSignInException(
    override val message: String? = "Sign in cancelled"
) : LoginException(message)