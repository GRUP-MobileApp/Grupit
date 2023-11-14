package com.grup.ui.models

import com.grup.exceptions.PendingTransactionRecordException
import com.grup.models.*
import com.grup.ui.compose.asMoneyAmount

internal sealed class Notification {
    abstract val date: String
    abstract val groupId: String
    abstract val user: User
    open val dismissible: Boolean = true
    abstract fun displayText(): String

    data class IncomingDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() = debtAction.date
        override val groupId: String
            get() = debtAction.groupId
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
        override val date: String
            get() =
                if (transactionRecord.isPending)
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for Settle Action " +
                                "with id ${debtAction.id}"
                    )
                else transactionRecord.dateAccepted

        override val groupId: String
            get() = debtAction.groupId
        override val user: User
            get() = transactionRecord.userInfo.user

        override fun displayText(): String =
            "${transactionRecord.userInfo.user.displayName} has accepted a debt of " +
                    "${transactionRecord.balanceChange.asMoneyAmount()} from you"
    }

    data class IncomingSettleAction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() = transactionRecord.dateCreated
        override val groupId: String
            get() = settleAction.groupId
        override val user: User
            get() = settleAction.userInfo.user
        override val dismissible: Boolean = false

        override fun displayText(): String =
            "${user.displayName} is settling for " +
                    transactionRecord.balanceChange.asMoneyAmount()
    }

    data class DebteeAcceptSettleAction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() =
                if (transactionRecord.isPending)
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for Settle Action " +
                                "with id ${settleAction.id}"
                    )
                 else transactionRecord.dateAccepted

        override val groupId: String
            get() = settleAction.groupId
        override val user: User
            get() = transactionRecord.userInfo.user

        override fun displayText(): String =
            "${user.displayName} accepted your settlement for " +
                    transactionRecord.balanceChange.asMoneyAmount()
    }

    data class IncomingGroupInvite(val groupInvite: GroupInvite) : Notification() {
        override val date: String
            get() = groupInvite.date
        override val groupId: String
            get() = groupInvite.groupId
        override val user: User
            get() = groupInvite.inviter
        override val dismissible: Boolean = false
        override fun displayText(): String =
            "${user.username} is inviting you to \"${groupInvite.groupName}\""
    }
}