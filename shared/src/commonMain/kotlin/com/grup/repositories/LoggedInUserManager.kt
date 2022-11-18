package com.grup.repositories

import com.grup.RealmUser
import com.grup.models.User
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.RealmSingleQuery

internal class LoggedInUserManager(val realmUser: RealmUser) {
    private val config = SyncConfiguration.Builder(realmUser, setOf(User::class))
        .maxNumberOfActiveVersions()
        .initialSubscriptions(rerunOnOpen = true) { realm ->
            add(realm.query(User::class, "realmId == $0", realmUser.id))
        }
        .name("loggedInUserRealm")
        .waitForInitialRemoteData()
        .build()

    private val realm: Realm by lazy { Realm.open(config) }

    val user: User by lazy { userQuery().find()!! }

    fun userQuery(): RealmSingleQuery<User> {
        return realm.query(User::class, "realmId == $0", realmUser.id).first()
    }

    fun registerUser(user: User) {
        realm.writeBlocking {
            copyToRealm(user.apply { this.realmId = realmUser.id })
        }
    }
}