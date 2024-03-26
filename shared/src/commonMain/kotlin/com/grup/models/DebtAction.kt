package com.grup.models


abstract class DebtAction internal constructor() : Action() {
    abstract val message: String

    final override val amount: Double
        get() = transactionRecords.sumOf { it.balanceChange }
    val acceptedAmount: Double
        get() = transactionRecords.filter {
            it.status is TransactionRecord.Status.Accepted
        }.sumOf { it.balanceChange }
}
