package com.grup.repositories

import com.grup.models.Group
import com.grup.models.User
import com.grup.models.realm.RealmUserInfo
import com.grup.other.toResolvedListFlow
import com.grup.repositories.abstract.RealmUserInfoRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.asQuery
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedUserInfoRepository: RealmUserInfoRepository(), KoinComponent {
    override val realm: Realm by inject()

    override suspend fun createUserInfo(user: User, group: Group): RealmUserInfo? {
        return super.createUserInfo(user, group).also {
            realm.syncSession.uploadAllLocalChanges()
        }
    }

    override fun findMyUserInfosAsFlow(): Flow<List<RealmUserInfo>> {
        return realm.subscriptions.findByName("MyUserInfos")!!
            .asQuery<RealmUserInfo>().toResolvedListFlow()
    }
}
