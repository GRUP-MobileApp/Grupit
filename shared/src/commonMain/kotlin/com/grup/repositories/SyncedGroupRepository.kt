package com.grup.repositories

import com.grup.repositories.abstract.RealmGroupRepository
import io.realm.kotlin.Realm
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedGroupRepository : RealmGroupRepository(), KoinComponent {
    override val realm: Realm by inject()
}
