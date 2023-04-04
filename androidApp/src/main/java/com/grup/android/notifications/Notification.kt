package com.grup.android.notifications

import com.grup.android.asMoneyAmount
import com.grup.exceptions.PendingTransactionRecordException
import com.grup.models.*

sealed class Notification {
    abstract val date: String
    abstract val groupId: String
    abstract val userInfo: UserInfo
    open val dismissible: Boolean = true
    abstract fun displayText(): String

    data class IncomingDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() = debtAction.date
        override val groupId: String
            get() = debtAction.groupId!!
        override val userInfo: UserInfo
            get() = debtAction.debteeUserInfo!!
        override val dismissible: Boolean = false

        override fun displayText(): String =
            "${debtAction.debteeUserInfo!!.nickname} is requesting " +
                    "${transactionRecord.balanceChange!!.asMoneyAmount()} from you"
    }

    data class DebtorAcceptOutgoingDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() = transactionRecord.let { transactionRecord ->
                if (!transactionRecord.isAccepted) {
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for Debt Action " +
                                "with id ${debtAction.getId()}"
                    )
                }
                transactionRecord.dateAccepted
            }
        override val groupId: String
            get() = debtAction.groupId!!
        override val userInfo: UserInfo
            get() = transactionRecord.debtorUserInfo!!

        override fun displayText(): String =
            "${transactionRecord.debtorUserInfo!!.nickname!!} has accepted a debt of " +
                    "${transactionRecord.balanceChange!!.asMoneyAmount()} from you"
    }

    data class IncomingTransactionOnSettleAction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() = transactionRecord.dateCreated
        override val groupId: String
            get() = settleAction.groupId!!
        override val userInfo: UserInfo
            get() = transactionRecord.debtorUserInfo!!
        override val dismissible: Boolean = false

        override fun displayText(): String =
            "${transactionRecord.debtorUserInfo!!.nickname!!} is settling " +
                    "${transactionRecord.balanceChange!!.asMoneyAmount()} out of your " +
                    "${settleAction.remainingAmount.asMoneyAmount()} request"
    }

    data class DebteeAcceptSettleActionTransaction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
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
        override val groupId: String
            get() = settleAction.groupId!!
        override val userInfo: UserInfo
            get() = settleAction.debteeUserInfo!!

        override fun displayText(): String =
            "${settleAction.debteeUserInfo!!.nickname} accepted your settlement for " +
                    transactionRecord.balanceChange!!.asMoneyAmount()
    }
}