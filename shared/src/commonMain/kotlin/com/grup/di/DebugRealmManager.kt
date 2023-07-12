package com.grup.di

import com.grup.exceptions.EntityAlreadyExistsException
import com.grup.exceptions.login.InvalidEmailPasswordException
import com.grup.other.TEST_APP_ID
import com.grup.interfaces.DBManager
import com.grup.platform.signin.AuthManager
import com.grup.service.Notifications
import io.realm.kotlin.mongodb.*
import io.realm.kotlin.mongodb.exceptions.BadRequestException
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

internal class DebugRealmManager private constructor(): RealmManager() {
    override val authProvider: AuthManager.AuthProvider
        get() = getAuthProvider(app)
    companion object {
        private val app: App = App.create(TEST_APP_ID)

        suspend fun silentSignIn(): DBManager? {
            return app.currentUser?.let { realmUser ->
                openRealm(realmUser)
                DebugRealmManager()
            }
        }

        suspend fun loginEmailPassword(email: String, password: String): DBManager {
            try {
                return loginRealmManager(Credentials.emailPassword(email, password))
            } catch (e: InvalidCredentialsException) {
                throw InvalidEmailPasswordException()
            }
        }

        suspend fun registerEmailPassword(email: String, password: String): DBManager {
            try {
                app.emailPasswordAuth.registerUser(email, password)
            } catch (e: UserAlreadyExistsException) {
                throw EntityAlreadyExistsException("Email already exists")
            } catch (e: BadRequestException) {
                // TODO: Bad email/bad password exception
            }
            return loginEmailPassword(email, password)
        }

        private suspend fun loginRealmManager(credentials: Credentials): DBManager {
            app.login(credentials).let { realmUser ->
                openRealm(realmUser)
                Notifications.subscribePersonalNotifications(realmUser.id)
            }
            return DebugRealmManager()
        }

        private suspend fun openRealm(realmUser: User) {
            RealmManager.openRealm(realmUser)
            loadKoinModules(debugAppModules)
        }
    }

    override suspend fun close() {
        super.close()
        unloadKoinModules(debugAppModules)
        app.currentUser?.logOut()
    }
}