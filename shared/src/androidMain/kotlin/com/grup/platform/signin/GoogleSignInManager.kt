package com.grup.platform.signin

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException

actual class GoogleSignInManager(
    private val googleSignInClient: GoogleSignInClient
) : SignInManager() {
    private lateinit var googleLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

    override fun signIn() {
        googleLauncher.launch(googleSignInClient.signInIntent)
    }

    override fun signOut() {
        googleSignInClient.signOut()
    }

    override fun doSilentSignIn(callback: (String) -> Unit) {
        googleSignInClient.silentSignIn().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? =
                    task.getResult(ApiException::class.java)
                val token: String = account?.idToken!!

                callback(token)
            }
        }
    }

    fun setGoogleLauncher(
        googleLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) {
        this.googleLauncher = googleLauncher
    }
}
