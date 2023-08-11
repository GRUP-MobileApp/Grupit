package com.grup.repositories

import com.grup.models.User
import com.grup.models.realm.RealmUserInfo
import com.grup.repositories.abstract.RealmUserInfoRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.syncSession
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedUserInfoRepository: RealmUserInfoRepository(), KoinComponent {
    override val realm: Realm by inject()

    override suspend fun createUserInfo(user: User, groupId: String): RealmUserInfo? {
        return super.createUserInfo(user, groupId).also {
            realm.syncSession.uploadAllLocalChanges()
        }
    }
}
