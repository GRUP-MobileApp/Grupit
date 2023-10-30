package com.grup.repositories.abstract

import com.grup.interfaces.ISettleActionRepository
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.models.realm.RealmDebtAction
import com.grup.models.realm.RealmSettleAction
import com.grup.models.realm.RealmUserInfo
import com.grup.other.copyNestedObjectToRealm
import com.grup.other.getLatestFields
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow

internal abstract class RealmSettleActionRepository : ISettleActionRepository {
    protected abstract val realm: Realm

    override suspend fun createSettleAction(
        debtor: UserInfo,
        transactionRecords: List<TransactionRecord>
    ): RealmSettleAction? {
        return realm.write {
            copyNestedObjectToRealm(
                RealmSettleAction().apply {
                    this._groupId = debtor.groupId
                    this._userInfo = debtor as RealmUserInfo
                    _transactionRecords.addAll(
                        transactionRecords.map { it.toRealmTransactionRecord() }
                    )
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

    override suspend fun addTransactionRecord(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ): SettleAction? {
        return realm.write {
            findLatest(settleAction as RealmSettleAction)!!.apply {
                _transactionRecords.add(
                    getLatestFields(transactionRecord.toRealmTransactionRecord())
                )
            }
        }
    }

    override fun findAllSettleActionsAsFlow(): Flow<List<RealmSettleAction>> {
        return realm.query<RealmSettleAction>().toResolvedListFlow()
    }
}
