package com.grup.android.transaction

import com.grup.exceptions.PendingTransactionRecordException
import com.grup.models.Action
import com.grup.models.DebtAction
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord

sealed class TransactionActivity {
    abstract val userId: String
    abstract val name: String
    abstract val date: String
    abstract fun displayText(): String

    data class CreateDebtAction(
        val debtAction: DebtAction
    ) : TransactionActivity() {
        override val userId: String
            get() = debtAction.debtee!!
        override val name: String
            get() = debtAction.debteeName!!
        override val date: String
            get() = debtAction.date

        override fun displayText() =
                    "$name created a transaction with ${debtAction.debtTransactions.size} people"
    }

    data class AcceptDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ): TransactionActivity() {
        override val userId: String
            get() = transactionRecord.debtor!!
        override val name: String
            get() = transactionRecord.debtorName!!
        override val date: String
            get() = transactionRecord.dateAccepted.also { dateAccepted ->
                if (dateAccepted == TransactionRecord.PENDING) {
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for Debt Action " +
                                "with id ${debtAction.getId()}"
                    )
                }
            }

        override fun displayText() =
                    "$name accepted a debt of ${transactionRecord.balanceChange} from" +
                    " ${debtAction.debteeName}"
    }

    data class CreateSettleAction(
        val settleAction: SettleAction
    ) : TransactionActivity() {
        override val userId: String
            get() = settleAction.debtee!!
        override val name: String
            get() = settleAction.debteeName!!
        override val date: String
            get() = settleAction.date

        override fun displayText() =
            "$name requested $${settleAction.settleAmount} to the group"
    }

    data class SettlePartialSettleAction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : TransactionActivity() {
        override val userId: String
            get() = transactionRecord.debtor!!
        override val name: String
            get() = transactionRecord.debtorName!!
        override val date: String
            get() = transactionRecord.dateAccepted.also { dateAccepted ->
                if (dateAccepted == TransactionRecord.PENDING) {
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for SettleAction " +
                                "with id ${settleAction.getId()}"
                    )
                }
            }
        override fun displayText(): String =
            "$name paid ${transactionRecord.balanceChange} to ${settleAction.debteeName}"
    }
}
