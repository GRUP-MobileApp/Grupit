package com.grup.platform.signin

abstract class SignInManager {
    abstract fun signIn()
    abstract fun signOut()

    abstract fun disconnect()
}