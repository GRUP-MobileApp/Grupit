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
        get() = transactionRecords.sumOf { it.balanceChange!! }
    val acceptedAmount
        get() = transactionRecords.sumOf { transactionRecord ->
            if (transactionRecord.dateAccepted != TransactionRecord.PENDING) {
                transactionRecord.balanceChange!!
            } else {
                0.0
            }
        }
}
