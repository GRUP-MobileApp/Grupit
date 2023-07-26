package com.grup.repositories.abstract

import com.grup.other.getLatestFields
import com.grup.interfaces.ISettleActionRepository
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal abstract class RealmSettleActionRepository : ISettleActionRepository {
    protected abstract val realm: Realm

    override suspend fun createSettleAction(settleAction: SettleAction): SettleAction? {
        return realm.write {
            copyToRealm(getLatestFields(settleAction))
        }
    }

    override suspend fun updateSettleAction(
        settleAction: SettleAction,
        block: SettleAction.() -> Unit
    ): SettleAction? {
        return realm.write {
            findLatest(settleAction)!!.apply(block)
        }
    }

    override fun addSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ): SettleAction? {
        return realm.writeBlocking {
            findLatest(settleAction)!!.apply {
                this.transactionRecords.add(
                    transactionRecord.apply {
                        this.debtorUserInfo = findLatest(this.debtorUserInfo!!)!!
                    }
                )
            }
        }
    }

    override fun findAllSettleActionsAsFlow(): Flow<List<SettleAction>> {
        return realm.query<SettleAction>().find().asFlow().map { it.list }
    }
}
