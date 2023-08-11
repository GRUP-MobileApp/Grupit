package com.grup.repositories

import com.grup.models.realm.RealmUser
import com.grup.repositories.abstract.RealmUserRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

internal class TestUserRepository : RealmUserRepository() {
    private val config = RealmConfiguration.Builder(schema = setOf(RealmUser::class)).build()
    override val realm: Realm by lazy { Realm.open(config) }

    override suspend fun findUserByUsername(username: String): RealmUser? {
        return realm.query<RealmUser>("username = $0", username).first().find()
    }
}