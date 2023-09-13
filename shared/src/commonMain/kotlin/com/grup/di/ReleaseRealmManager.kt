package com.grup.di

import com.grup.exceptions.login.InvalidGoogleAccountException
import com.grup.interfaces.DBManager
import io.realm.kotlin.mongodb.*
import io.realm.kotlin.mongodb.exceptions.AuthException

internal class ReleaseRealmManager private constructor() : RealmManager() {
    companion object {
        suspend fun silentSignIn(): DBManager? {
            return releaseApp.currentUser?.let {
                ReleaseRealmManager().apply { open() }
            }
        }

        suspend fun loginGoogle(googleAccountToken: String): DBManager {
            try {
                return loginRealmManager(
                    Credentials.google(googleAccountToken, GoogleAuthType.ID_TOKEN)
                )
            } catch (e: AuthException) {
                throw InvalidGoogleAccountException(e.message)
            }
        }

        private suspend fun loginRealmManager(credentials: Credentials): DBManager {
            releaseApp.login(credentials)
            return ReleaseRealmManager().apply { open() }
        }
    }
}