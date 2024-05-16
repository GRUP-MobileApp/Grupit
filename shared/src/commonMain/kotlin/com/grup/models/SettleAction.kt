package com.grup.models

import kotlin.math.max

abstract class SettleAction internal constructor() : Action() {
    abstract override val transactionRecords: MutableList<TransactionRecord>

    abstract override var amount: Double
    val acceptedAmount: Double
        get() = transactionRecords.filter {
            it.status is TransactionRecord.Status.Accepted
        }.sumOf { it.balanceChange }
    val pendingAmount: Double
        get() = transactionRecords.filter {
            it.status is TransactionRecord.Status.Pending
        }.sumOf { it.balanceChange }
    val remainingAmount: Double
        get() = max(amount - acceptedAmount - pendingAmount, 0.0)
    val isCompleted: Boolean
        get() = acceptedAmount == amount
}
