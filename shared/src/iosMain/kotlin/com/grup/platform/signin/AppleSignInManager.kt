package com.grup.platform.signin

import com.grup.device.SettingsManager
import com.grup.exceptions.login.CancelledSignInException
import com.grup.exceptions.login.SignInException
import kotlinx.coroutines.delay
import platform.AuthenticationServices.ASAuthorizationController

actual class AppleSignInManager(
    getAuthorizationController: () -> ASAuthorizationController
) : SignInManager() {
    private val authorizationController: ASAuthorizationController by
        lazy { getAuthorizationController() }

    override suspend fun signIn(block: (String) -> Unit) {
        try {
            authorizationController.performRequests()
        } catch (e: Exception) {
            throw SignInException()
        }

        repeat(4 * 60 * 5) {
            with(SettingsManager.LoginSettings) {
                if (isAppleSignInSuccess != null) {
                    if (isAppleSignInSuccess == true) {
                        appleToken?.let(block)
                    }
                    appleToken = null
                    if (isAppleSignInSuccess == false) {
                        isAppleSignInSuccess = null
                        throw CancelledSignInException()
                    }
                    isAppleSignInSuccess = null
                }
            }
            delay(250)
        }
        throw SignInException("Timed out")
    }

    override suspend fun signOut() { }

    override fun disconnect() { }
}