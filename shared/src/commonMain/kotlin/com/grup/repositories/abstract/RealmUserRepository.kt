package com.grup.repositories.abstract

import com.grup.interfaces.IUserRepository
import com.grup.models.User
import com.grup.models.realm.RealmUser
import com.grup.other.copyNestedObjectToRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.asQuery

internal abstract class RealmUserRepository : IUserRepository {
    protected abstract val realm: Realm

    override suspend fun createMyUser(
        username: String,
        displayName: String,
        venmoUsername: String?
    ): RealmUser? {
        return realm.write {
            copyNestedObjectToRealm(
                RealmUser().apply {
                    this._username = username
                    this._displayName = displayName
                    this._venmoUsername = venmoUsername
                }
            )
        }
    }

    override fun findMyUser(): RealmUser? {
        return realm.subscriptions.findByName("MyUser")!!.asQuery<RealmUser>().first().find()
    }

    override suspend fun updateUser(user: User, block: User.() -> Unit): RealmUser {
        return realm.write {
            findLatest(user as RealmUser)!!.apply(block)
        }
    }
}