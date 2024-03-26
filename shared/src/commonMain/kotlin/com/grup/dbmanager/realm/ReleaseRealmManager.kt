package com.grup.dbmanager.realm

import com.grup.exceptions.login.InvalidGoogleAccountException
import com.grup.dbmanager.DatabaseManager
import com.grup.dbmanager.RealmManager
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import io.realm.kotlin.mongodb.exceptions.AuthException

internal class ReleaseRealmManager private constructor() : RealmManager() {
    companion object {
        suspend fun silentSignIn(): DatabaseManager? {
            return releaseApp.currentUser?.let {
                ReleaseRealmManager().apply { open() }
            }
        }

        suspend fun loginGoogle(googleAccountToken: String): DatabaseManager {
            try {
                return loginRealmManager(
                    Credentials.google(googleAccountToken, GoogleAuthType.ID_TOKEN)
                )
            } catch (e: AuthException) {
                throw InvalidGoogleAccountException(e.message)
            }
        }

        private suspend fun loginRealmManager(credentials: Credentials): DatabaseManager {
            releaseApp.login(credentials)
            return ReleaseRealmManager().apply { open() }
        }
    }
}