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

    abstract class CreateAction<T: Action> (
        open val action: T
    ): TransactionActivity() {
        override val userId: String
            get() = action.debtee!!
        override val name: String
            get() = action.debteeName!!
        override val date: String
            get() = action.date
    }

    abstract class AcceptAction<T: Action> (
        private val action: T,
        private val transactionRecord: TransactionRecord
    ) : TransactionActivity() {
        override val userId: String
            get() = transactionRecord.debtor!!
        override val name: String
            get() = transactionRecord.debtorName!!
        override val date: String
            get() = transactionRecord.dateAccepted.also { dateAccepted ->
                if (dateAccepted == TransactionRecord.PENDING) {
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for " +
                                when(action) {
                                    is DebtAction -> "DebtAction"
                                    is SettleAction -> "SettleAction"
                                    else -> ""
                                } +
                                " with id ${action.getId()}"
                    )
                }
            }
    }

    data class CreateDebtAction(
        val debtAction: DebtAction
    ) : CreateAction<DebtAction>(debtAction) {
        override fun displayText() =
                    "$name created a transaction with ${debtAction.debtTransactions.size} people"
    }

    data class AcceptDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ): AcceptAction<DebtAction>(debtAction, transactionRecord) {
        override fun displayText() =
                    "$name accepted a debt of ${transactionRecord.balanceChange} from" +
                    " ${debtAction.debteeName}"
    }

    data class CreateSettleAction(
        val settleAction: SettleAction
    ) : CreateAction<SettleAction>(settleAction) {
        override fun displayText() =
            "$name created a transaction with ${settleAction.debtTransactions.size} people"
    }

    data class SettlePartialSettleAction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : AcceptAction<SettleAction>(settleAction, transactionRecord) {
        override fun displayText(): String =
            ""
    }
}
