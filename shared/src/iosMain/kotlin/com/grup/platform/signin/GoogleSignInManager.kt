package com.grup.platform.signin

actual class GoogleSignInManager(
    private val signInClosure: ((String) -> Unit) -> Unit,
    private val signOutClosure: () -> Unit,
    private val disconnectClosure: () -> Unit
) : SignInManager() {
    private lateinit var signInCallback: (String) -> Unit
    override fun signIn() = signInClosure(signInCallback)

    override fun signOut() = signOutClosure()

    override fun disconnect() = disconnectClosure()

    fun setSignInCallback(signInCallback: (String) -> Unit) {
        this.signInCallback = signInCallback
    }
}