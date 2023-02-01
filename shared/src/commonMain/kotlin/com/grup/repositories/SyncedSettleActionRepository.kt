package com.grup.repositories

import com.grup.repositories.abstract.RealmSettleActionRepository
import io.realm.kotlin.Realm
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedSettleActionRepository
    : RealmSettleActionRepository(), KoinComponent {
    override val realm: Realm by inject()
}
