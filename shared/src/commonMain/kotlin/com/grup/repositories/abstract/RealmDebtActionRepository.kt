package com.grup.repositories.abstract

import com.grup.interfaces.IDebtActionRepository
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.models.realm.RealmDebtAction
import com.grup.models.realm.RealmUserInfo
import com.grup.other.copyNestedObjectToRealm
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow

internal abstract class RealmDebtActionRepository : IDebtActionRepository {
    protected abstract val realm: Realm

    override fun createDebtAction(
        debtee: UserInfo,
        transactionRecords: List<TransactionRecord>,
        message: String
    ): RealmDebtAction? {
        return realm.writeBlocking {
            copyNestedObjectToRealm(
                RealmDebtAction().apply {
                    _userInfo = debtee as RealmUserInfo
                    _group = debtee._group
                    _groupId = debtee.group.id
                    _transactionRecords.addAll(
                        transactionRecords.map { it.toRealmTransactionRecord() }
                    )
                    _message = message
                }
            )
        }
    }

    override suspend fun updateDebtAction(
        debtAction: DebtAction,
        block: DebtAction.() -> Unit
    ): RealmDebtAction? {
        return realm.write {
            findLatest(debtAction as RealmDebtAction)!!.apply(block)
        }
    }

    override fun findAllDebtActionsAsFlow(): Flow<List<RealmDebtAction>> {
        return realm.query<RealmDebtAction>().toResolvedListFlow()
    }
}
