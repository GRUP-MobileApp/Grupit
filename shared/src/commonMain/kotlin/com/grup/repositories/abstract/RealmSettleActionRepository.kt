package com.grup.repositories.abstract

import com.grup.interfaces.ISettleActionRepository
import com.grup.models.SettleAction
import com.grup.models.UserInfo
import com.grup.models.realm.RealmSettleAction
import com.grup.models.realm.RealmUserInfo
import com.grup.other.copyNestedObjectToRealm
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow

internal abstract class RealmSettleActionRepository : ISettleActionRepository {
    protected abstract val realm: Realm

    override suspend fun createSettleAction(
        settleAmount: Double,
        debtee: UserInfo
    ): RealmSettleAction? {
        return realm.write {
            copyNestedObjectToRealm(
                RealmSettleAction().apply {
                    this._groupId = debtee.groupId
                    this._debteeUserInfo = debtee as RealmUserInfo
                    this._settleAmount = settleAmount
                }
            )
        }
    }

    override suspend fun updateSettleAction(
        settleAction: SettleAction,
        block: SettleAction.() -> Unit
    ): RealmSettleAction? {
        return realm.write {
            findLatest(settleAction as RealmSettleAction)!!.apply(block)
        }
    }

    override fun findAllSettleActionsAsFlow(): Flow<List<RealmSettleAction>> {
        return realm.query<RealmSettleAction>().toResolvedListFlow()
    }
}
