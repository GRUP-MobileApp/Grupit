package com.grup.repositories

import com.grup.models.TransactionRecord
import com.grup.repositories.abstract.RealmTransactionRecordRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class TestTransactionRecordRepository : RealmTransactionRecordRepository() {
    private val config = RealmConfiguration.Builder(schema = setOf(TransactionRecord::class)).build()
    override val realm: Realm by lazy { Realm.open(config) }
}