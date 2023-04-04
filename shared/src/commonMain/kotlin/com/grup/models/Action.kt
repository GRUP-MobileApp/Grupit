package com.grup.models

import io.realm.kotlin.types.RealmList

sealed class Action : BaseEntity() {
    abstract var groupId: String?
        internal set
    abstract var debteeUserInfo: UserInfo?
        internal set
    abstract var date: String
        internal set
    abstract var transactionRecords: RealmList<TransactionRecord>
        internal set

    val totalAmount
        get() = transactionRecords.filter { transactionRecord ->
            transactionRecord.dateAccepted != TransactionRecord.REJECTED
        }.sumOf { it.balanceChange!! }
    val acceptedAmount
        get() = transactionRecords.filter { transactionRecord ->
            transactionRecord.isAccepted
        }.sumOf { it.balanceChange!! }
}
