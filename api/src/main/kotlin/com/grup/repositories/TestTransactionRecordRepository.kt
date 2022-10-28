package com.grup.repositories

import com.grup.interfaces.ITransactionRecordRepository
import com.grup.models.TransactionRecord
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class TestTransactionRecordRepository : ITransactionRecordRepository {
    private val config = RealmConfiguration.Builder(schema = setOf(TransactionRecord::class)).build()
    private val transactionRecordRealm: Realm = Realm.open(config)

    override fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord? {
        return transactionRecordRealm.writeBlocking {
            copyToRealm(transactionRecord)
        }
    }
}