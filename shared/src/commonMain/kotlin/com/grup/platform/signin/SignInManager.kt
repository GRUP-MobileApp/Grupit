package com.grup.platform.signin


abstract class SignInManager {
    abstract suspend fun signIn(block: (String) -> Unit)
    abstract suspend fun signOut()

    abstract fun disconnect()
}