package com.grup.repositories

import com.grup.models.realm.RealmUserInfo
import com.grup.other.toResolvedListFlow
import com.grup.repositories.abstract.RealmUserInfoRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.asQuery
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedUserInfoRepository: RealmUserInfoRepository(), KoinComponent {
    override val realm: Realm by inject()

    override fun findMyUserInfosAsFlow(excludeInactive: Boolean): Flow<List<RealmUserInfo>> =
        if (excludeInactive) {
            realm.subscriptions.findByName("MyUserInfos")!!
                .asQuery<RealmUserInfo>().toResolvedListFlow()
        } else {
            realm.query<RealmUserInfo>("userId == $0", realm.syncSession.user.id)
                .toResolvedListFlow()
        }
}
