package com.grup.models

import io.realm.kotlin.types.RealmList

sealed class Action : BaseEntity() {
    abstract var date: String
        internal set
    abstract var groupId: String?
        internal set
    abstract var groupName: String?
        internal set
    abstract var debtee: String?
        internal set
    abstract var debteeName: String?
        internal set
    abstract var debtTransactions: RealmList<TransactionRecord>
        internal set

    val totalAmount
        get() = debtTransactions.sumOf { it.balanceChange!! }
    val acceptedAmount
        get() = debtTransactions.sumOf { transactionRecord ->
            if (transactionRecord.dateAccepted != TransactionRecord.PENDING) {
                transactionRecord.balanceChange!!
            } else {
                0.0
            }
        }
}
