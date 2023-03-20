package com.grup.repositories.abstract

import com.grup.models.User
import com.grup.repositories.UserRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

internal abstract class RealmUserRepository : UserRepository() {
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
        return realm.query<User>().first().find()
    }

    override suspend fun updateUser(user: User, block: User.() -> Unit) {
        realm.write {
            findLatest(user)!!.apply(block)
        }
    }
}