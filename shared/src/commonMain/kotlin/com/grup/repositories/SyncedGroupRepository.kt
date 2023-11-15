package com.grup.repositories

import com.grup.models.realm.RealmGroup
import com.grup.models.realm.RealmUserInfo
import com.grup.other.toResolvedListFlow
import com.grup.repositories.abstract.RealmGroupRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.asQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedGroupRepository : RealmGroupRepository(), KoinComponent {
    override val realm: Realm by inject()

    override fun findAllGroupsAsFlow(): Flow<List<RealmGroup>> {
        return super.findAllGroupsAsFlow().combine(
            realm.subscriptions.findByName("MyUserInfos")!!
                .asQuery<RealmUserInfo>().toResolvedListFlow()
        ) { groups, userInfos ->
            groups.filter { group ->
                userInfos.any { userInfo ->
                    group.id == userInfo._groupId
                }
            }
        }
    }
}
