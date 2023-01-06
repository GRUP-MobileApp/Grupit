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
        return realm.writeBlocking {
            copyToRealm(userInfo)
        }
    }

    override fun findUserInfoByUser(userId: String, groupId: String): UserInfo? {
        return realm.query<UserInfo>("userId == $0 AND groupId == $1", userId, groupId)
            .first().find()
    }

    override fun findUserInfosByGroupIdAsFlow(groupId: String): Flow<List<UserInfo>> {
        return realm.query<UserInfo>("groupId == $0", groupId).find().asFlow().map { it.list }
    }

    override fun updateUserInfo(userInfo: UserInfo, block: (UserInfo) -> Unit): UserInfo? {
        return realm.writeBlocking {
            findLatest(userInfo)?.apply {
                block(this)
            }
        }
    }

    override fun close() {
        realm.close()
    }
}