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
            copyToRealm(debtAction)
        }
    }

    override fun getAllDebtActionsAsFlow(): Flow<List<DebtAction>> {
        return realm.query<DebtAction>().find().asFlow().map { it.list }
    }

    override fun close() {
        realm.close()
    }
}