package com.grup.platform.signin

abstract class SignInManager {
    abstract fun signIn()
    abstract fun signOut()

    private var didSilentSignIn: Boolean = false
    fun silentSignIn(callback: (token: String) -> Unit) {
        if (!didSilentSignIn) {
            didSilentSignIn = true
            doSilentSignIn(callback)
        }
    }

    protected abstract fun doSilentSignIn(callback: (token: String) -> Unit)
}