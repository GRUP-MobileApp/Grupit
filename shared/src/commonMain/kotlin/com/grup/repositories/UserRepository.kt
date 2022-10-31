package com.grup.repositories

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import com.grup.objects.idSerialName
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class UserRepository(
    config: RealmConfiguration = RealmConfiguration.Builder(schema = setOf(User::class)).build()
) : IUserRepository {
    private val userRealm: Realm = Realm.open(config)

    override fun insertUser(user: User): User? {
        return userRealm.writeBlocking {
            copyToRealm(user)
        }
    }

    override fun findUserById(userId: String): User? {
        return userRealm.query(User::class, "$idSerialName == $0", userId).first().find()
    }

    override fun findUserByUserName(username: String): User? {
        return userRealm.query(User::class, "username == $0", username).first().find()
    }

    override fun close() {
        userRealm.close()
    }
}