package com.grup.platform.signin

import cocoapods.GoogleSignIn.GIDSignIn
import com.grup.exceptions.login.InvalidGoogleAccountException
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow

@OptIn(ExperimentalForeignApi::class)
actual class GoogleSignInManager: SignInManager() {
    override suspend fun signIn(block: (String) -> Unit) {
        GIDSignIn.sharedInstance.signInWithPresentingViewController(
            (UIApplication.sharedApplication.windows as List<UIWindow>)
                .first().rootViewController ?: throw InvalidGoogleAccountException()
        ) { signInResult, error ->
            if (error != null) {
                return@signInWithPresentingViewController
            }
            signInResult?.user?.idToken?.tokenString?.let { token ->
                block(token)
            }
        }
    }

    override suspend fun signOut() {
        GIDSignIn.sharedInstance.signOut()
    }

    override fun disconnect() {
        GIDSignIn.sharedInstance.disconnectWithCompletion {  }
    }
}
