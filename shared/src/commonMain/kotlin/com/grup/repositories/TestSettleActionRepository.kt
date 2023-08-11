package com.grup.repositories

import com.grup.models.realm.RealmSettleAction
import com.grup.repositories.abstract.RealmSettleActionRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class TestSettleActionRepository : RealmSettleActionRepository() {
    private val config = RealmConfiguration.Builder(schema = setOf(RealmSettleAction::class)).build()
    override val realm: Realm by lazy { Realm.open(config) }
}
