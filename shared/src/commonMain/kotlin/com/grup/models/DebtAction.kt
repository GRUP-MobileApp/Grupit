package com.grup.models


abstract class DebtAction internal constructor() : Action() {
    enum class Platform {
        Grupit, Venmo
    }
    abstract val message: String
    abstract val platform: Platform

    final override val amount: Double
        get() = transactionRecords.sumOf { it.balanceChange }
    val acceptedAmount: Double
        get() = transactionRecords.filter {
            it.status is TransactionRecord.Status.Accepted
        }.sumOf { it.balanceChange }
}
