package com.grup.platform.signin

import cocoapods.GoogleSignIn.GIDSignIn
import com.grup.exceptions.login.CancelledSignInException
import com.grup.exceptions.login.SignInException
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow

@OptIn(ExperimentalForeignApi::class)
actual class GoogleSignInManager: SignInManager() {
    override suspend fun signIn(block: suspend (String, String?) -> Unit) {
        var token: String? = null
        var name: String? = null
        var error: NSError? = null

        var isComplete = false
        GIDSignIn.sharedInstance.signInWithPresentingViewController(
            (UIApplication.sharedApplication.windows().first() as? UIWindow)
                ?.rootViewController
                ?: throw SignInException()
        ) { signInResult, err ->
            if (err != null) {
                error = err
            } else {
                token = signInResult?.user?.idToken?.tokenString
                name = signInResult?.user?.profile?.name
            }
            isComplete = true
        }

        repeat(4 * 60 * 3) {
            if (isComplete) {
                if (error != null) {
                    throw CancelledSignInException()
                }

                token?.let { block(it, name) } ?: throw SignInException()

                return
            }
            delay(250)
        }
    }

    override suspend fun signOut() {
        GIDSignIn.sharedInstance.signOut()
    }

    override fun disconnect() {
        GIDSignIn.sharedInstance.disconnectWithCompletion { }
    }
}
