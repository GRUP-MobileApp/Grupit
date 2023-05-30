package com.grup.platform.signin

data class AuthManager(
    val googleSignInManager: GoogleSignInManager? = null,
    val appleSignInManager: AppleSignInManager? = null
) {
    sealed class AuthProvider {
        object Google : AuthProvider()
        object Apple : AuthProvider()
        object EmailPassword : AuthProvider()
        object EmailPasswordRegister : AuthProvider()
        object None : AuthProvider()
    }

    fun getSignInManagerFromProvider(authProvider: AuthProvider) = when(authProvider) {
        AuthProvider.Apple -> googleSignInManager
        AuthProvider.Google -> appleSignInManager
        else -> null
    }
}