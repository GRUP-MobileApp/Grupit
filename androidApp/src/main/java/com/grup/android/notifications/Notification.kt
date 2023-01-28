package com.grup.android.notifications

import com.grup.exceptions.PendingTransactionRecordException
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.GroupInvite

sealed class Notification {
    abstract val date: String
    abstract fun displayText(): String

    data class IncomingGroupInvite(
        private val groupInvite: GroupInvite
    ) : Notification() {
        override val date: String
            get() = groupInvite.date

        override fun displayText(): String =
            "${groupInvite.inviterUsername} invited you to ${groupInvite.groupName}"
    }

    data class InviteeAcceptOutgoingGroupInvite(
        private val groupInvite: GroupInvite
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
            "${debtAction.debteeName} is requesting ${transactionRecord.balanceChange} from you"
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
            "${transactionRecord.debtorName} has accepted a debt of " +
                    "${transactionRecord.balanceChange} from you"
    }
}