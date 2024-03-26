package com.grup.repositories.abstract

import com.grup.dbmanager.RealmManager
import com.grup.interfaces.ISettleActionRepository
import com.grup.models.SettleAction
import com.grup.models.UserInfo
import com.grup.models.realm.RealmSettleAction
import com.grup.models.realm.RealmUserInfo
import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import com.grup.other.getLatest
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow

internal abstract class RealmSettleActionRepository : ISettleActionRepository {
    protected abstract val realm: Realm
    override fun createSettleAction(
        transaction: DatabaseWriteTransaction,
        debtee: UserInfo,
        settleActionAmount: Double
    ): RealmSettleAction? = with(transaction as RealmManager.RealmWriteTransaction) {
        copyToRealm(
            getLatest(
                RealmSettleAction(
                    userInfo = debtee as RealmUserInfo,
                    amount = settleActionAmount
                )
            ),
            UpdatePolicy.ERROR
        )
    }

    override fun updateSettleAction(
        transaction: DatabaseWriteTransaction,
        settleAction: SettleAction,
        block: SettleAction.() -> Unit
    ): RealmSettleAction? = with(transaction as RealmManager.RealmWriteTransaction) {
        findLatest(settleAction as RealmSettleAction)!!.apply(block)
    }

    override fun findAllSettleActionsAsFlow(): Flow<List<RealmSettleAction>> =
        realm.query<RealmSettleAction>().toResolvedListFlow()
}
