package com.grup.ui.models

import com.grup.exceptions.PendingTransactionRecordException
import com.grup.models.*
import com.grup.ui.compose.asMoneyAmount

internal sealed class TransactionActivity {
    abstract val action: Action
    abstract val userInfo: UserInfo
    abstract val date: String
    abstract val amount: Double
    abstract val activityName: String
    abstract fun displayText(): String

    data class CreateDebtAction(
        val debtAction: DebtAction
    ) : TransactionActivity() {
        override val action: Action
            get() = debtAction
        override val userInfo: UserInfo
            get() = debtAction.debteeUserInfo!!
        override val date: String
            get() = debtAction.date
        override val amount: Double
            get() = debtAction.totalAmount
        override val activityName: String
            get() = "Debt Request"

        override fun displayText() = "\"${debtAction.message}\""
    }

    data class CreateSettleAction(
        val settleAction: SettleAction
    ) : TransactionActivity() {
        override val action: Action
            get() = settleAction
        override val userInfo: UserInfo
            get() = settleAction.debteeUserInfo!!
        override val date: String
            get() = settleAction.date
        override val amount: Double
            get() = settleAction.totalAmount
        override val activityName: String
            get() = "New Settle"

        override fun displayText() = "created a new settlement"
    }

    data class SettlePartialSettleAction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : TransactionActivity() {
        override val action: Action
            get() = settleAction
        override val userInfo: UserInfo
            get() = transactionRecord.debtorUserInfo!!
        override val date: String
            get() = transactionRecord.let { transactionRecord ->
                if (!transactionRecord.isAccepted) {
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for Settle Action " +
                                "with id ${settleAction.getId()}"
                    )
                }
                transactionRecord.dateAccepted
            }
        override val amount: Double
            get() = transactionRecord.balanceChange!!
        override val activityName: String
            get() = "Settle Request"

        override fun displayText(): String =
            "paid ${settleAction.debteeUserInfo!!.nickname!!}"
    }
}
