package com.grup.repositories

import com.grup.APIServer.Login.app
import com.grup.models.User
import com.grup.repositories.abstract.RealmUserRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.syncSession
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedUserRepository : KoinComponent, RealmUserRepository() {
    override val realm: Realm by inject()
    private val myUserId: String by lazy { app.currentUser!!.id }

    override suspend fun createMyUser(
        username: String,
        displayName: String
    ): User {
        return realm.write {
            copyToRealm(
                User(myUserId).apply {
                    this.username = username
                    this.displayName = displayName
                }
            )
        }.also {
            realm.syncSession.uploadAllLocalChanges()
            realm.syncSession.downloadAllServerChanges()
        }
    }
}