package com.grup.repositories.abstract

import com.grup.interfaces.ITransactionRecordRepository
import com.grup.models.TransactionRecord
import io.realm.kotlin.Realm

internal abstract class RealmTransactionRecordRepository : ITransactionRecordRepository {
    protected abstract val realm: Realm

    override fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord? {
        return realm.writeBlocking {
            copyToRealm(transactionRecord)
        }
    }

    override fun close() {
        realm.close()
    }
}