package com.grup.platform.signin

import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName

actual class AppleSignInManager : SignInManager() {
    private val appleIDProvider: ASAuthorizationAppleIDProvider = ASAuthorizationAppleIDProvider()

    override suspend fun signIn(block: (String) -> Unit) {
        val request = appleIDProvider.createRequest()
        request.setRequestedScopes(listOf(ASAuthorizationScopeFullName, ASAuthorizationScopeEmail))

        val authorizationController = ASAuthorizationController(listOf(request))

        TODO("Finish")
    }

    override suspend fun signOut() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }
}