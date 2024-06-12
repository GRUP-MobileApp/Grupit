package com.grup.platform.signin

actual class AppleSignInManager : SignInManager() {
    override suspend fun signIn(block: suspend (String, String?) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }
}