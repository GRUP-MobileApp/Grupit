package com.grup.di

import com.grup.exceptions.login.InvalidGoogleAccountException
import com.grup.interfaces.DBManager
import com.grup.other.APP_ID
import com.grup.platform.signin.AuthManager
import com.grup.service.Notifications
import io.realm.kotlin.mongodb.*
import io.realm.kotlin.mongodb.exceptions.AuthException
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

internal class ReleaseRealmManager private constructor() : RealmManager() {
    override val authProvider: AuthManager.AuthProvider
        get() = getAuthProvider(app)
    companion object {
        private val app: App = App.create(APP_ID)

        suspend fun silentSignIn(): DBManager? {
            return app.currentUser?.let { realmUser ->
                openRealm(realmUser)
                ReleaseRealmManager()
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
            app.login(credentials).let { realmUser ->
                openRealm(realmUser)
                Notifications.subscribePersonalNotifications(realmUser.id)
            }
            return ReleaseRealmManager()
        }

        private suspend fun openRealm(realmUser: User) {
            RealmManager.openRealm(realmUser)
            loadKoinModules(releaseAppModules)
        }
    }

    override suspend fun close() {
        super.close()
        unloadKoinModules(releaseAppModules)
        app.currentUser?.logOut()
    }
}