package com.grup.repositories.abstract

import com.grup.interfaces.ISettleActionRepository
import com.grup.models.SettleAction
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal abstract class RealmSettleActionRepository : ISettleActionRepository {
    protected abstract val realm: Realm

    override fun createSettleAction(settleAction: SettleAction): SettleAction? {
        return realm.writeBlocking {
            settleAction.debteeUserInfo = findLatest(settleAction.debteeUserInfo!!)!!
            settleAction.debtTransactions.forEachIndexed { i, _ ->
                settleAction.debtTransactions[i].apply {
                    this.debtorUserInfo = findLatest(this.debtorUserInfo!!)!!
                }
            }
            copyToRealm(settleAction)
        }
    }

    override fun updateSettleAction(
        settleAction: SettleAction,
        block: SettleAction.() -> Unit
    ): SettleAction? {
        return realm.writeBlocking {
            findLatest(settleAction)?.apply(block)
        }
    }

    override fun findAllSettleActionsAsFlow(): Flow<List<SettleAction>> {
        return realm.query<SettleAction>().find().asFlow().map { it.list }
    }
}
