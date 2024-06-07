package com.grup.platform.signin

import cocoapods.GoogleSignIn.GIDSignIn
import com.grup.exceptions.login.CancelledSignInException
import com.grup.exceptions.login.SignInException
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow

@OptIn(ExperimentalForeignApi::class)
actual class GoogleSignInManager: SignInManager() {
    override suspend fun signIn(block: (String, String?) -> Unit) {
        var signInError: NSError? = null
        GIDSignIn.sharedInstance.signInWithPresentingViewController(
            (UIApplication.sharedApplication.windows.first() as? UIWindow)
                ?.rootViewController
                ?: throw SignInException()
        ) { signInResult, error ->
            if (error != null) {
                signInError = error
            } else {
                signInResult?.user?.idToken?.tokenString?.let { token ->
                    block(token, signInResult.user.profile?.name)
                }
            }
        }
        if (signInError != null) {
            throw CancelledSignInException()
        }
    }

    override suspend fun signOut() {
        GIDSignIn.sharedInstance.signOut()
    }

    override fun disconnect() {
        GIDSignIn.sharedInstance.disconnectWithCompletion { }
    }
}
