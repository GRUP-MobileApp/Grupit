package com.grup.exceptions.login

internal class SignInException(
    override val message: String? = "Sign in error occurred. Please try again later"
) : LoginException(message)