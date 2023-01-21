package com.grup.android

import com.grup.models.DebtAction
import com.grup.models.TransactionRecord

sealed class TransactionActivity {
    abstract val userId: String
    abstract val name: String
    abstract val date: String
    abstract fun displayText(asPersonal: Boolean = false): String
}

class CreateDebtAction(
    private val debtAction: DebtAction
) : TransactionActivity() {
    override val userId: String
        get() = debtAction.debtee!!
    override val name: String
        get() = debtAction.debteeName!!
    override val date: String
        get() = debtAction.date

    override fun displayText(asPersonal: Boolean) =
        (if (asPersonal) "You" else name) +
                " created a transaction with ${debtAction.debtTransactions.size} people"
}

class AcceptDebtAction(
    private val debtAction: DebtAction,
    private val transactionRecord: TransactionRecord
): TransactionActivity() {
    override val userId: String
        get() = transactionRecord.debtor!!
    override val name: String
        get() = transactionRecord.debtorName!!
    override val date: String
        get() = transactionRecord.dateAccepted.also {
            if (it == "PENDING") {
                throw Exception()
            }
        }

    override fun displayText(asPersonal: Boolean) =
        (if (asPersonal) "You" else name) +
                " accepted a debt of ${transactionRecord.balanceChange} from" +
                " ${debtAction.debteeName}"
}
