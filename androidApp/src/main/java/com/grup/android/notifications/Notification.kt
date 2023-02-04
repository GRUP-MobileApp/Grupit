package com.grup.android.notifications

import com.grup.exceptions.PendingTransactionRecordException
import com.grup.models.*

sealed class Notification {
    abstract val date: String
    abstract fun displayText(): String

    data class IncomingGroupInvite(
        val groupInvite: GroupInvite
    ) : Notification() {
        override val date: String
            get() = groupInvite.date

        override fun displayText(): String =
            "${groupInvite.inviterUsername} invited you to ${groupInvite.groupName}"
    }

    data class InviteeAcceptOutgoingGroupInvite(
        val groupInvite: GroupInvite
    ) : Notification() {
        override val date: String
            get() = groupInvite.dateAccepted.also { date ->
                if (date == GroupInvite.PENDING) {
                    throw PendingTransactionRecordException(
                        "GroupInvite with id ${groupInvite.getId()} still pending"
                    )
                }
            }

        override fun displayText(): String =
            "${groupInvite.inviteeUsername} has accepted your invite to ${groupInvite.groupName}"
    }

    data class IncomingDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() = debtAction.date

        override fun displayText(): String =
            "${debtAction.debteeUserInfo!!.nickname} is requesting " +
                    "${transactionRecord.balanceChange} from you"
    }

    data class DebtorAcceptOutgoingDebtAction(
        val debtAction: DebtAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() = transactionRecord.dateAccepted.also { date ->
                if (date == TransactionRecord.PENDING) {
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for" +
                                "DebtAction with id ${debtAction.getId()}"
                    )
                }
            }

        override fun displayText(): String =
            "${transactionRecord.debtorUserInfo!!.nickname!!} has accepted a debt of " +
                    "${transactionRecord.balanceChange} from you"
    }

    data class NewSettleAction(
        val settleAction: SettleAction
    ) : Notification() {
        override val date: String
            get() = settleAction.date

        override fun displayText(): String =
            "${settleAction.debteeUserInfo!!.nickname!!} is requesting ${settleAction.settleAmount}"
    }

    data class IncomingTransactionOnSettleAction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() = transactionRecord.dateCreated

        override fun displayText(): String =
            "${transactionRecord.debtorUserInfo!!.nickname!!} is settling " +
                    "$${transactionRecord.balanceChange} out of your " +
                    "$${settleAction.settleAmount} request"
    }

    data class DebteeAcceptSettleActionTransaction(
        val settleAction: SettleAction,
        val transactionRecord: TransactionRecord
    ) : Notification() {
        override val date: String
            get() = transactionRecord.dateAccepted.also { date ->
                if (date == TransactionRecord.PENDING) {
                    throw PendingTransactionRecordException(
                        "TransactionRecord still pending for" +
                                "DebtAction with id ${settleAction.getId()}"
                    )
                }
            }

        override fun displayText(): String =
            "${settleAction.debteeUserInfo!!.nickname} accepted your settlement for " +
                    "${transactionRecord.balanceChange}"
    }
}