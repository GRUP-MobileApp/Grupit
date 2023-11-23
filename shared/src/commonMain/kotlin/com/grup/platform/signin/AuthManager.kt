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
        data object Google : AuthProvider()
        @SerialName("Apple")
        data object Apple : AuthProvider()
        @SerialName("EmailPassword")
        data object EmailPassword : AuthProvider()
        @SerialName("EmailPasswordRegister")
        data object EmailPasswordRegister : AuthProvider()
        @SerialName("None")
        data object None : AuthProvider()
    }

    fun getSignInManagerFromProvider(authProvider: AuthProvider) = when(authProvider) {
        AuthProvider.Apple -> appleSignInManager
        AuthProvider.Google -> googleSignInManager
        else -> null
    }
}