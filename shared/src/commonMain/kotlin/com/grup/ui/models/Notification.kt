package com.grup.ui.models

import com.grup.exceptions.PendingTransactionRecordException
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.ui.compose.asMoneyAmount
import kotlinx.datetime.Instant

internal sealed class Notification {
    abstract val date: Instant
    abstract val group: Group
    abstract val user: User
    open val dismissible: Boolean = true
    abstract fun displayText(): String

    data class IncomingDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        init {
            if (transactionRecord.status != TransactionRecord.Status.Pending)
                throw PendingTransactionRecordException(
                    "TransactionRecord still pending for DebtAction " +
                            "with id ${debtAction.id}"
                )
        }
        override val date: Instant
            get() = debtAction.date
        override val group: Group
            get() = debtAction.userInfo.group
        override val user: User
            get() = debtAction.userInfo.user
        override val dismissible: Boolean = false

        override fun displayText(): String =
            "${debtAction.userInfo.user.displayName} is requesting " +
                    "${transactionRecord.balanceChange.asMoneyAmount()} from you"
    }

    data class DebtorAcceptOutgoingDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: Instant
            get() = when(transactionRecord.status) {
                is TransactionRecord.Status.Accepted ->
                    (transactionRecord.status as TransactionRecord.Status.Accepted).date
                is TransactionRecord.Status.Rejected ->
                    (transactionRecord.status as TransactionRecord.Status.Rejected).date
                else -> throw PendingTransactionRecordException(
                    "TransactionRecord still pending for DebtAction with id ${debtAction.id}"
                )
            }

        override val group: Group
            get() = debtAction.userInfo.group
        override val user: User
            get() = transactionRecord.userInfo.user

        override fun displayText(): String =
            "${transactionRecord.userInfo.user.displayName} has " +
                    when(transactionRecord.status) {
                        is TransactionRecord.Status.Accepted -> "accepted"
                        is TransactionRecord.Status.Rejected -> "rejected"
                        is TransactionRecord.Status.Pending -> throw PendingTransactionRecordException(
                            "TransactionRecord still pending for DebtAction with id ${debtAction.id}"
                        )
                    } +
                    " a debt of ${transactionRecord.balanceChange.asMoneyAmount()} from you"
    }

    data class NewSettleAction(val settleAction: SettleAction) : Notification() {
        init {
            if (settleAction.isCompleted)
                throw IllegalArgumentException(
                    "Settle action with id ${settleAction.id} is already completed"
                )
        }
        override val date: Instant
            get() = settleAction.date
        override val group: Group
            get() = settleAction.userInfo.group
        override val user: User
            get() = settleAction.userInfo.user

        override fun displayText(): String =
            "${user.displayName} is settling for " +
                    "${settleAction.amount.asMoneyAmount()} in ${group.groupName}"
    }

    data class IncomingSettleActionTransaction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        init {
            if (transactionRecord.status != TransactionRecord.Status.Pending)
                throw PendingTransactionRecordException(
                    "TransactionRecord not pending for SettleAction " +
                            "with id ${settleAction.id}"
                )
        }
        override val date: Instant
            get() = transactionRecord.dateCreated
        override val group: Group
            get() = settleAction.userInfo.group
        override val user: User
            get() = settleAction.userInfo.user
        override val dismissible: Boolean = false

        override fun displayText(): String = "${transactionRecord.userInfo.user.displayName} is " +
                "settling for ${transactionRecord.balanceChange.asMoneyAmount()}"
    }

    data class DebteeAcceptOutgoingSettleActionTransaction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: Instant
            get() = when(transactionRecord.status) {
                is TransactionRecord.Status.Accepted ->
                    (transactionRecord.status as TransactionRecord.Status.Accepted).date
                is TransactionRecord.Status.Rejected ->
                    (transactionRecord.status as TransactionRecord.Status.Rejected).date
                else -> throw PendingTransactionRecordException(
                    "TransactionRecord still pending for SettleAction with id ${settleAction.id}"
                )
            }

        override val group: Group
            get() = settleAction.userInfo.group
        override val user: User
            get() = transactionRecord.userInfo.user

        override fun displayText(): String =
            "${user.displayName} " +
                    when(transactionRecord.status) {
                        is TransactionRecord.Status.Accepted -> "accepted"
                        is TransactionRecord.Status.Rejected -> "rejected"
                        is TransactionRecord.Status.Pending -> throw PendingTransactionRecordException(
                            "TransactionRecord still pending for SettleAction with id " +
                                    settleAction.id
                        )
                    } + " your settlement for " +
                    transactionRecord.balanceChange.asMoneyAmount()
    }

    data class IncomingGroupInvite(val groupInvite: GroupInvite) : Notification() {
        override val date: Instant
            get() = groupInvite.date
        override val group: Group
            get() = groupInvite.group
        override val user: User
            get() = groupInvite.inviter
        override val dismissible: Boolean = false
        override fun displayText(): String =
            "${user.username} is inviting you to \"${groupInvite.group.groupName}\""
    }
}