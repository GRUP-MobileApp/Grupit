package com.grup.repositories.abstract

import com.grup.dbmanager.RealmManager
import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import com.grup.interfaces.IUserRepository
import com.grup.models.User
import com.grup.models.realm.RealmUser
import com.grup.other.getLatest
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.asQuery

internal abstract class RealmUserRepository : IUserRepository {
    protected abstract val realm: Realm

    override fun createMyUser(
        transaction: DatabaseWriteTransaction,
        username: String,
        displayName: String,
        venmoUsername: String?
    ): RealmUser? = with(transaction as RealmManager.RealmWriteTransaction) {
        copyToRealm(
            getLatest(
                RealmUser(username = username).apply {
                    this.displayName = displayName
                    this.venmoUsername = venmoUsername ?: "None"
                }
            ),
            UpdatePolicy.ERROR
        )
    }

    override fun findMyUser(): RealmUser? {
        return realm.subscriptions.findByName("MyUser")!!.asQuery<RealmUser>().first().find()
    }

    override fun updateUser(
        transaction: DatabaseWriteTransaction,
        user: User,
        block: User.() -> Unit
    ): RealmUser = with(transaction as RealmManager.RealmWriteTransaction) {
        findLatest(user as RealmUser)!!.apply(block)
    }
}