package com.grup.dbmanager.realm

import com.grup.dbmanager.DatabaseManager
import com.grup.dbmanager.RealmManager
import com.grup.exceptions.login.SignInException
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
                throw SignInException("Internal error, try again later.")
            }
        }

        suspend fun loginApple(appleAccountToken: String): DatabaseManager {
            try {
                return loginRealmManager(
                    Credentials.apple(appleAccountToken)
                )
            } catch (e: AuthException) {
                throw SignInException("Internal error, try again later.")
            }
        }

        private suspend fun loginRealmManager(credentials: Credentials): DatabaseManager {
            releaseApp.login(credentials)
            return ReleaseRealmManager().apply { open() }
        }
    }
}