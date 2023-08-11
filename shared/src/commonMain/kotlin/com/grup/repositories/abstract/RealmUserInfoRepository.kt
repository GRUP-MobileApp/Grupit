package com.grup.repositories.abstract

import com.grup.interfaces.IUserInfoRepository
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.models.realm.RealmUser
import com.grup.models.realm.RealmUserInfo
import com.grup.other.copyNestedObjectToRealm
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.asQuery
import kotlinx.coroutines.flow.Flow

internal abstract class RealmUserInfoRepository : IUserInfoRepository {
    protected abstract val realm: Realm

    override suspend fun createUserInfo(user: User, groupId: String): RealmUserInfo? {
        // TODO: Check that you only have <= 3 userInfos at a time
        return realm.write {
            copyNestedObjectToRealm(
                RealmUserInfo(user as RealmUser).apply {
                    _groupId = groupId
                }
            )
        }
    }

    override fun findUserInfosByGroupId(groupId: String): List<RealmUserInfo> {
        return realm.query<RealmUserInfo>("_groupId == $0", groupId).find()
    }

    override fun findMyUserInfosAsFlow(): Flow<List<RealmUserInfo>> {
        return realm.subscriptions.findByName("UserInfos")!!
            .asQuery<RealmUserInfo>().toResolvedListFlow()
    }

    override fun findAllUserInfosAsFlow(): Flow<List<RealmUserInfo>> {
        return realm.query<RealmUserInfo>().toResolvedListFlow()
    }

    override suspend fun updateUserInfo(
        userInfo: UserInfo,
        block: (UserInfo) -> Unit
    ): RealmUserInfo? {
        return realm.write {
            findLatest(userInfo as RealmUserInfo)!!.apply(block)
        }
    }
}