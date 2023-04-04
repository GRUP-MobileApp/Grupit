package com.grup.repositories.abstract

import com.grup.di.getLatestFields
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
            copyToRealm(getLatestFields(debtAction))
        }
    }

    override suspend fun updateDebtAction(
        debtAction: DebtAction,
        block: DebtAction.() -> Unit
    ): DebtAction? {
        return realm.write {
            findLatest(debtAction)!!.apply(block)
        }
    }

    override fun findAllDebtActionsAsFlow(): Flow<List<DebtAction>> {
        return realm.query<DebtAction>().find().asFlow().map { it.list }
    }
}
