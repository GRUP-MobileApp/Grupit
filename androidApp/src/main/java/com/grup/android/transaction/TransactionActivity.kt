package com.grup.android.transaction

import com.grup.exceptions.PendingTransactionRecordException
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
            get() = debtAction.debteeUserInfo!!.userId!!
        override val name: String
            get() = debtAction.debteeUserInfo!!.nickname!!
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
            get() = transactionRecord.debtorUserInfo!!.getId()
        override val name: String
            get() = transactionRecord.debtorUserInfo!!.nickname!!
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
                    " ${debtAction.debteeUserInfo!!.nickname!!}"
    }

    data class CreateSettleAction(
        val settleAction: SettleAction
    ) : TransactionActivity() {
        override val userId: String
            get() = settleAction.debteeUserInfo!!.userId!!
        override val name: String
            get() = settleAction.debteeUserInfo!!.nickname!!
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
            get() = transactionRecord.debtorUserInfo!!.getId()
        override val name: String
            get() = transactionRecord.debtorUserInfo!!.nickname!!
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
            "$name paid ${transactionRecord.balanceChange} to ${settleAction.debteeUserInfo!!.nickname!!}"
    }
}
