package com.grup.repositories.abstract

import com.grup.interfaces.IDebtActionRepository
import com.grup.models.DebtAction
import io.realm.kotlin.ext.query
import io.realm.kotlin.Realm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal abstract class RealmDebtActionRepository : IDebtActionRepository {
    protected abstract val realm: Realm

    override fun createDebtAction(debtAction: DebtAction): DebtAction? {
        return realm.writeBlocking {
            debtAction.debteeUserInfo = findLatest(debtAction.debteeUserInfo!!)!!
            debtAction.debtTransactions.forEachIndexed { i, _ ->
                debtAction.debtTransactions[i].apply {
                    this.debtorUserInfo = findLatest(this.debtorUserInfo!!)!!
                }
            }
            copyToRealm(debtAction)
        }
    }

    override fun updateDebtAction(debtAction: DebtAction,
                                  block: DebtAction.() -> Unit): DebtAction? {
        return realm.writeBlocking {
            findLatest(debtAction)?.apply(block)
        }
    }

    override fun findAllDebtActionsAsFlow(): Flow<List<DebtAction>> {
        return realm.query<DebtAction>().find().asFlow().map { it.list }
    }
}