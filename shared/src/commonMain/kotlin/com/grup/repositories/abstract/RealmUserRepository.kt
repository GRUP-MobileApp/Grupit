package com.grup.repositories.abstract

import com.grup.interfaces.IUserRepository
import com.grup.models.User
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.asQuery

internal abstract class RealmUserRepository : IUserRepository {
    protected abstract val realm: Realm

    override suspend fun createMyUser(
        username: String,
        displayName: String
    ): User? {
        return realm.write {
            copyToRealm(
                User().apply {
                    this.username = username
                    this.displayName = displayName
                }
            )
        }
    }

    override fun findMyUser(): User? {
        return realm.subscriptions.findByName("MyUser")!!.asQuery<User>().first().find()
    }

    override suspend fun updateUser(user: User, block: User.() -> Unit) {
        realm.write {
            findLatest(user)!!.apply(block)
        }
    }
}