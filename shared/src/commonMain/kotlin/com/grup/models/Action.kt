package com.grup.models

sealed class Action : BaseEntity() {
    abstract val userInfo: UserInfo
    abstract val group: Group
    abstract val transactionRecords: List<TransactionRecord>
    abstract val date: String

    val totalAmount
        get() = transactionRecords.filter { transactionRecord ->
            transactionRecord.dateAccepted != TransactionRecord.REJECTED
        }.sumOf { it.balanceChange }
    val acceptedAmount
        get() = transactionRecords.filter { transactionRecord ->
            transactionRecord.isAccepted
        }.sumOf { it.balanceChange }
}
