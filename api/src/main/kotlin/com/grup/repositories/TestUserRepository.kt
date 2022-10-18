package com.grup.repositories

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class TestUserRepository : IUserRepository {
    private val config = RealmConfiguration.Builder(schema = setOf(User::class)).build()
    private val userRealm: Realm = Realm.open(config)

    override fun insertUser(user: User): User? {
        return userRealm.writeBlocking {
            copyToRealm(user)
        }
    }

    override fun findUserById(userId: String): User? {
        return userRealm.query(User::class, "id == $0", userId).first().find()
    }

    override fun findUserByUserName(username: String): User? {
        return userRealm.query(User::class, "username == $0", username).first().find()
    }
}