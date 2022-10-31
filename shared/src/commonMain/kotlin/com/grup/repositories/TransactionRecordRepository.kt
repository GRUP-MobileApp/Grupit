package com.grup.repositories

import com.grup.interfaces.ITransactionRecordRepository
import com.grup.models.TransactionRecord
import com.grup.models.UserBalance
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class TransactionRecordRepository(
    config: RealmConfiguration = RealmConfiguration.Builder(schema = setOf(UserBalance::class)).build()
) : ITransactionRecordRepository {
    private val transactionRecordRealm: Realm = Realm.open(config)

    override fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord? {
        return transactionRecordRealm.writeBlocking {
            copyToRealm(transactionRecord)
        }
    }

    override fun close() {
        transactionRecordRealm.close()
    }
}