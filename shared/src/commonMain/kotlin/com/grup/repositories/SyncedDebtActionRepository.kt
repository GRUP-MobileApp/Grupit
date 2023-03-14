package com.grup.repositories

import com.grup.repositories.abstract.RealmDebtActionRepository
import io.realm.kotlin.Realm
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedDebtActionRepository : RealmDebtActionRepository(), KoinComponent {
    override val realm: Realm by inject()
}
