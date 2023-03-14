package com.grup.repositories

import com.grup.models.UserInfo
import com.grup.repositories.abstract.RealmUserInfoRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedUserInfoRepository: RealmUserInfoRepository(), KoinComponent {
    override val realm: Realm by inject()

    override fun createUserInfo(userInfo: UserInfo): UserInfo? {
        return super.createUserInfo(userInfo).also {
            runBlocking {
                realm.syncSession.uploadAllLocalChanges()
            }
        }
    }
}
