package com.grup.models

import com.grup.exceptions.NegativeBalanceException

abstract class SettleAction : Action() {
    abstract val settleAmount: Double
    abstract override val transactionRecords: MutableList<TransactionRecord>
    val remainingAmount: Double
        get() = (settleAmount - acceptedAmount).also { remainingAmount ->
            if (remainingAmount < 0) {
                throw NegativeBalanceException("SettleAction with id $_id has negative remaining " +
                        "balance")
            }
        }
}
