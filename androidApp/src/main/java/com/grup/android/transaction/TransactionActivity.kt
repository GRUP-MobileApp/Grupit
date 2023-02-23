package com.grup.android.transaction

import com.grup.android.asMoneyAmount
import com.grup.exceptions.PendingTransactionRecordException
import com.grup.models.*

sealed class TransactionActivity {
    abstract val action: Action
    abstract val userInfo: UserInfo
    abstract val date: String
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

        override fun displayText() =
                    "${userInfo.nickname!!} created a transaction with " +
                            "${debtAction.transactionRecords.size} people"
    }

    data class AcceptDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ): TransactionActivity() {
        override val action: Action
            get() = debtAction
        override val userInfo: UserInfo
            get() = transactionRecord.debtorUserInfo!!
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
                    "${userInfo.nickname!!} accepted a debt of " +
                            "${transactionRecord.balanceChange!!.asMoneyAmount()} from " +
                            debtAction.debteeUserInfo!!.nickname!!
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

        override fun displayText() =
            "${userInfo.nickname!!} requested ${settleAction.settleAmount!!.asMoneyAmount()} " +
                    "to the group"
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
            get() = transactionRecord.dateAccepted.also { dateAccepted ->
                if (dateAccepted == TransactionRecord.PENDING) {
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for SettleAction " +
                                "with id ${settleAction.getId()}"
                    )
                }
            }
        override fun displayText(): String =
            "${userInfo.nickname!!} paid ${transactionRecord.balanceChange!!.asMoneyAmount()} to " +
                    settleAction.debteeUserInfo!!.nickname!!
    }
}
