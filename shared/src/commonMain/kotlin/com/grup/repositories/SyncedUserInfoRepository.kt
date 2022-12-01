package com.grup.repositories

import com.grup.repositories.abstract.RealmUserInfoRepository
import io.realm.kotlin.Realm
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedUserInfoRepository: RealmUserInfoRepository(), KoinComponent {
    override val realm: Realm by inject()
}
