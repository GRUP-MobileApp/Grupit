package com.grup.repositories.abstract

import com.grup.interfaces.IUserInfoRepository
import com.grup.models.UserInfo
import com.grup.other.Id
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

internal abstract class RealmUserInfoRepository : IUserInfoRepository {
    protected abstract val realm: Realm

    override fun createUserInfo(userInfo: UserInfo): UserInfo? {
        return realm.writeBlocking {
            copyToRealm(userInfo)
        }
    }

    override fun findUserInfoByUser(userId: Id, groupId: Id): UserInfo? {
        return realm.query<UserInfo>("userId == $0 AND groupId == $1", userId, groupId)
            .first().find()
    }

    override fun findUserInfosByGroup(groupId: Id): List<UserInfo> {
        return realm.query<UserInfo>("groupId == $0", groupId).find().toList()
    }

    override fun updateUserInfo(userInfo: UserInfo): UserInfo? {
        return realm.writeBlocking {
            copyToRealm(userInfo, UpdatePolicy.ALL)
        }
    }

    override fun close() {
        realm.close()
    }
}