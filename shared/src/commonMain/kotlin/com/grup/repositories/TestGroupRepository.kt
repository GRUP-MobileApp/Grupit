package com.grup.repositories

import com.grup.models.Group
import com.grup.repositories.abstract.RealmGroupRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class TestGroupRepository : RealmGroupRepository() {
    private val config  = RealmConfiguration.Builder(schema = setOf(Group::class)).build()
    override val realm: Realm by lazy { Realm.open(config) }
}