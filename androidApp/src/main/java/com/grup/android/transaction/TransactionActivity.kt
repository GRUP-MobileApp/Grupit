package com.grup.android.transaction

import com.grup.exceptions.PendingTransactionRecordException
import com.grup.models.DebtAction
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
                        "TransactionRecord still pending for" +
                                "DebtAction with id ${debtAction.getId()}"
                    )
                }
            }

        override fun displayText() =
                    "$name accepted a debt of ${transactionRecord.balanceChange} from" +
                    " ${debtAction.debteeName}"
    }
}
