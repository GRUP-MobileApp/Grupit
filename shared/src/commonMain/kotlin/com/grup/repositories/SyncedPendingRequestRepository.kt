package com.grup.repositories

import com.grup.repositories.abstract.RealmPendingRequestRepository
import io.realm.kotlin.Realm
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedPendingRequestRepository : RealmPendingRequestRepository(), KoinComponent {
    override val realm: Realm by inject()
}