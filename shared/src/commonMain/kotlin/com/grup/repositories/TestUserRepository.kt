package com.grup.repositories

import com.grup.models.User
import com.grup.models.realm.RealmUser
import com.grup.repositories.abstract.RealmUserRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query

internal class TestUserRepository : RealmUserRepository() {
    private val config = RealmConfiguration.Builder(schema = setOf(RealmUser::class)).build()
    override val realm: Realm by lazy { Realm.open(config) }
}