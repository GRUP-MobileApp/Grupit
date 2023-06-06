package com.grup.platform.signin

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class AuthManager(
    val googleSignInManager: GoogleSignInManager? = null,
    val appleSignInManager: AppleSignInManager? = null
) {
    @Serializable
    sealed class AuthProvider {
        @SerialName("Google")
        object Google : AuthProvider()
        @SerialName("Apple")
        object Apple : AuthProvider()
        @SerialName("EmailPassword")
        object EmailPassword : AuthProvider()
        @SerialName("EmailPasswordRegister")
        object EmailPasswordRegister : AuthProvider()
        @SerialName("None")
        object None : AuthProvider()
    }

    fun getSignInManagerFromProvider(authProvider: AuthProvider) = when(authProvider) {
        AuthProvider.Apple -> appleSignInManager
        AuthProvider.Google -> googleSignInManager
        else -> null
    }
}