package com.grup.dbmanager.realm

import com.grup.dbmanager.DatabaseManager
import com.grup.dbmanager.RealmManager
import com.grup.exceptions.login.InvalidEmailPasswordException
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.exceptions.BadRequestException
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException

internal class DebugRealmManager private constructor(): RealmManager(true) {
    companion object {
        suspend fun silentSignIn(): DatabaseManager? {
            return debugApp.currentUser?.let {
                DebugRealmManager().apply { open() }
            }
        }

        suspend fun loginEmailPassword(email: String, password: String): DatabaseManager {
            try {
                return loginRealmManager(Credentials.emailPassword(email, password))
            } catch (e: InvalidCredentialsException) {
                throw InvalidEmailPasswordException()
            }
        }

        suspend fun registerEmailPassword(email: String, password: String): DatabaseManager {
            try {
                debugApp.emailPasswordAuth.registerUser(email, password)
            } catch (e: UserAlreadyExistsException) {
                throw InvalidEmailPasswordException("Email already exists")
            } catch (e: BadRequestException) {
                // TODO: Bad email/bad password exception
            }
            return loginEmailPassword(email, password)
        }

        private suspend fun loginRealmManager(credentials: Credentials): DatabaseManager {
            debugApp.login(credentials)
            return DebugRealmManager().apply { open() }
        }
    }
}
