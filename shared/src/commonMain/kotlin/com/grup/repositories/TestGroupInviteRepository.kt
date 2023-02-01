package com.grup.repositories

import com.grup.models.GroupInvite
import com.grup.repositories.abstract.RealmGroupInviteRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class TestGroupInviteRepository : RealmGroupInviteRepository() {
    private val config = RealmConfiguration.Builder(schema = setOf(GroupInvite::class)).build()
    override val realm: Realm by lazy { Realm.open(config) }
}
