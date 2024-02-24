package com.grup.platform.signin

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
import cocoapods.GoogleSignIn.GIDSignIn
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow

@OptIn(ExperimentalForeignApi::class)
actual class GoogleSignInManager: SignInManager() {
    private lateinit var signInCallback: (String) -> Unit
    override fun signIn() {
        GIDSignIn.sharedInstance.signInWithPresentingViewController(
            (UIApplication.sharedApplication.windows as List<UIWindow>)
                .first().rootViewController ?: throw Exception()
        ) { signInResult, error ->
            if (error != null) {
                throw Exception()
            }

            signInResult?.user?.let { user ->
                FIRAuth.auth().signInWithCredential(
                    FIRGoogleAuthProvider.credentialWithIDToken(
                        IDToken = user.idToken?.tokenString ?: throw Exception(),
                        accessToken = user.accessToken.tokenString
                    )
                ) { _, _ ->
                    signInCallback(user.accessToken.tokenString)
                }
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun signOut() {
        GIDSignIn.sharedInstance.signOut()
        FIRAuth.auth().signOut(null)
    }

    override fun disconnect() {
        GIDSignIn.sharedInstance.disconnectWithCompletion {  }
    }

    fun setSignInCallBack(signInCallback: (String) -> Unit) {
        this.signInCallback = signInCallback
    }
}
