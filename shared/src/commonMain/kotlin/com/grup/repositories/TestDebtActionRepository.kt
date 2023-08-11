package com.grup.repositories

import com.grup.models.realm.RealmDebtAction
import com.grup.repositories.abstract.RealmDebtActionRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class TestDebtActionRepository : RealmDebtActionRepository() {
    private val config = RealmConfiguration.Builder(schema = setOf(RealmDebtAction::class)).build()
    override val realm: Realm by lazy { Realm.open(config) }
}
