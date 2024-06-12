package com.grup.platform.signin


abstract class SignInManager {
    abstract suspend fun signIn(block: suspend (String, String?) -> Unit)
    abstract suspend fun signOut()

    abstract fun disconnect()
}