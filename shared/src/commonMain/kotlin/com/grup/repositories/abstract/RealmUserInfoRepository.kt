package com.grup.repositories.abstract

import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import com.grup.dbmanager.RealmManager
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.Group
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.models.realm.RealmGroup
import com.grup.models.realm.RealmUser
import com.grup.models.realm.RealmUserInfo
import com.grup.other.getLatest
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.TRUE_PREDICATE
import kotlinx.coroutines.flow.Flow

internal abstract class RealmUserInfoRepository : IUserInfoRepository {
    protected abstract val realm: Realm
    override fun createUserInfo(
        transaction: DatabaseWriteTransaction,
        user: User,
        group: Group
    ): RealmUserInfo? = with(transaction as RealmManager.RealmWriteTransaction) {
        copyToRealm(
            getLatest(
                RealmUserInfo(
                    user = user as RealmUser,
                    group = group as RealmGroup
                )
            ),
            UpdatePolicy.ERROR
        )
    }

    override fun findMyUserInfosAsFlow(excludeInactive: Boolean): Flow<List<RealmUserInfo>> =
        realm.query<RealmUserInfo>(
            if (excludeInactive) "isActive == true"
            else TRUE_PREDICATE
        ).toResolvedListFlow()


    override fun findAllUserInfosAsFlow(): Flow<List<RealmUserInfo>> =
        realm.query<RealmUserInfo>("isActive == true").toResolvedListFlow()

    override fun updateUserInfo(
        transaction: DatabaseWriteTransaction,
        userInfo: UserInfo,
        block: UserInfo.() -> Unit,
    ): RealmUserInfo? = with(transaction as RealmManager.RealmWriteTransaction) {
        findLatest(userInfo as RealmUserInfo)!!.apply(block)
    }
}