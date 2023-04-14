package com.grup.repositories.abstract

import com.grup.interfaces.IUserInfoRepository
import com.grup.models.UserInfo
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal abstract class RealmUserInfoRepository : IUserInfoRepository {
    protected abstract val realm: Realm

    override fun createUserInfo(userInfo: UserInfo): UserInfo? {
        // TODO: Check that you only have <= 3 userInfos at a time
        return realm.writeBlocking {
            copyToRealm(userInfo)
        }
    }

    override fun findUserInfosByGroupId(groupId: String): List<UserInfo> {
        return realm.query<UserInfo>("groupId == $0", groupId).find()
    }

    override fun findMyUserInfosAsFlow(userId: String): Flow<List<UserInfo>> {
        return realm.query<UserInfo>("userId == $0", userId).find().asFlow().map { it.list }
    }

    override fun findAllUserInfosAsFlow(): Flow<List<UserInfo>> {
        return realm.query<UserInfo>().find().asFlow().map { it.list }
    }

    override suspend fun updateUserInfo(userInfo: UserInfo, block: (UserInfo) -> Unit): UserInfo? {
        return realm.write {
            findLatest(userInfo)?.apply {
                block(this)
            }
        }
    }
}