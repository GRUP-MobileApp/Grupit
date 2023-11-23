package com.grup.repositories.abstract

import com.grup.interfaces.IUserInfoRepository
import com.grup.models.Group
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.models.realm.RealmGroup
import com.grup.models.realm.RealmUser
import com.grup.models.realm.RealmUserInfo
import com.grup.other.copyNestedObjectToRealm
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow

internal abstract class RealmUserInfoRepository : IUserInfoRepository {
    protected abstract val realm: Realm

    override suspend fun createUserInfo(user: User, group: Group): RealmUserInfo? {
        // TODO: Check that you only have <= 3 userInfos at a time
        return realm.write {
            copyNestedObjectToRealm(
                RealmUserInfo().apply {
                    _user = user as RealmUser
                    _userId = user.id
                    _group = group as RealmGroup
                    _groupId = group.id
                }
            )
        }
    }

    override fun findUserInfosByGroupId(groupId: String): List<RealmUserInfo> {
        return realm.query<RealmUserInfo>("_groupId == $0", groupId).find()
    }

    override fun findMyUserInfosAsFlow(): Flow<List<RealmUserInfo>> {
        return realm.query<RealmUserInfo>().toResolvedListFlow()
    }

    override fun findAllUserInfosAsFlow(): Flow<List<RealmUserInfo>> {
        return realm.query<RealmUserInfo>().toResolvedListFlow()
    }

    override suspend fun updateUserInfo(
        userInfo: UserInfo,
        block: (UserInfo) -> Unit,
    ): RealmUserInfo? {
        return realm.write {
            findLatest(userInfo as RealmUserInfo)!!.apply(block)
        }
    }
}