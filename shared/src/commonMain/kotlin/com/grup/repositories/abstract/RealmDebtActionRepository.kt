package com.grup.repositories.abstract

import com.grup.dbmanager.RealmManager
import com.grup.interfaces.IDebtActionRepository
import com.grup.models.DebtAction
import com.grup.models.UserInfo
import com.grup.models.realm.RealmDebtAction
import com.grup.models.realm.RealmUserInfo
import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import com.grup.models.TransactionRecord
import com.grup.other.getLatest
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow

internal abstract class RealmDebtActionRepository : IDebtActionRepository {
    protected abstract val realm: Realm

    override fun createDebtAction(
        transaction: DatabaseWriteTransaction,
        debtee: UserInfo,
        transactionRecords: List<TransactionRecord>,
        message: String
    ): RealmDebtAction? = with(transaction as RealmManager.RealmWriteTransaction) {
        copyToRealm(
            getLatest(
                RealmDebtAction(
                    userInfo = debtee as RealmUserInfo,
                    message = message,
                    transactionRecords = transactionRecords
                )
            ),
            UpdatePolicy.ERROR
        )
    }

    override fun updateDebtAction(
        transaction: DatabaseWriteTransaction,
        debtAction: DebtAction,
        block: DebtAction.() -> Unit
    ): RealmDebtAction? = with(transaction as RealmManager.RealmWriteTransaction) {
        findLatest(debtAction as RealmDebtAction)!!.apply(block)
    }

    override fun findAllDebtActionsAsFlow(): Flow<List<RealmDebtAction>> {
        return realm.query<RealmDebtAction>().toResolvedListFlow()
    }
}
