package com.grup.repositories

import com.grup.exceptions.DoesNotExistException
import com.grup.models.User
import com.grup.interfaces.IUserRepository
import com.grup.other.Id
import com.grup.other.idSerialName
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

internal class UserRepository : IUserRepository {
    private val config = RealmConfiguration.Builder(schema = setOf(User::class)).build()
    private val userRealm: Realm = Realm.open(config)

    override fun insertUser(user: User): User? {
        return userRealm.writeBlocking {
            copyToRealm(user)
        }
    }

    override fun findUserById(userId: Id): User? {
        return userRealm.query<User>("$idSerialName == $0", userId).first().find()
    }

    override fun findUserByUserName(username: String): User? {
        return userRealm.query<User>("username == $0", username).first().find()
    }

    // TODO: Change
    override fun updateUser(user: User): User? {
        findUserById(user.getId())
            ?: throw DoesNotExistException("User with id ${user.getId()} does not exist")
        return userRealm.writeBlocking {
            copyToRealm(user, UpdatePolicy.ALL)
        }
    }

    override fun close() {
        userRealm.close()
    }
}
