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

    override suspend fun signIn(block: suspend (String, String?) -> Unit) {
        try {
            SettingsManager.LoginSettings.appleSignInStatus = null
            authorizationController.performRequests()
        } catch (e: Exception) {
            throw SignInException()
        }

        // Check local settings for updates to sign in status
        repeat( 4 * 60 * 3) {
            with(SettingsManager.LoginSettings.appleSignInStatus) {
                if (this != null) {
                    (this as? AppleSignInResult.Success)
                        ?.let { block(it.appleToken, it.fullName) }

                    if (this is AppleSignInResult.Failed) {
                        throw CancelledSignInException()
                    }
                    return
                }
            }
            delay(250)
        }
        throw SignInException("Timed out")
    }

    override suspend fun signOut() { }

    override fun disconnect() { }
}