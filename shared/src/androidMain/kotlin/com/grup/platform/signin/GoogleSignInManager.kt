package com.grup.platform.signin

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest.Builder
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.grup.exceptions.login.SignInException
import com.grup.other.GOOGLE_SERVER_CLIENT_ID
import java.security.MessageDigest
import java.util.UUID

actual class GoogleSignInManager(private val context: Context): SignInManager() {
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    override suspend fun signIn(block: (String) -> Unit) {
        try {
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(GOOGLE_SERVER_CLIENT_ID)
                .setNonce(generateNonce())
                .build()

            val result: GetCredentialResponse = credentialManager.getCredential(
                request = Builder()
                    .addCredentialOption(googleIdOption)
                    .build(),
                context = context,
            )
            block(GoogleIdTokenCredential.createFrom(result.credential.data).idToken)
        } catch (e: GetCredentialException) {
            try {
                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(GOOGLE_SERVER_CLIENT_ID)
                    .setNonce(generateNonce())
                    .build()

                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = Builder()
                        .addCredentialOption(googleIdOption)
                        .build(),
                    context = context,
                )

                block(GoogleIdTokenCredential.createFrom(result.credential.data).idToken)
            } catch (e: Exception) {
                throw SignInException()
            }
        } catch (e: GoogleIdTokenParsingException) {
            throw SignInException()
        }
    }

    override suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    override fun disconnect() { }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
