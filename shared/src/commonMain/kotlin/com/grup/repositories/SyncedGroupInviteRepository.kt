package com.grup.repositories

import com.grup.repositories.abstract.RealmGroupInviteRepository
import io.realm.kotlin.Realm
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedGroupInviteRepository : RealmGroupInviteRepository(), KoinComponent {
    override val realm: Realm by inject()
}