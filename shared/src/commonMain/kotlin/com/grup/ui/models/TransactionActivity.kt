package com.grup.ui.models

import com.grup.models.Action
import com.grup.models.DebtAction
import com.grup.models.SettleAction
import com.grup.models.UserInfo
import kotlinx.datetime.Instant

internal sealed class TransactionActivity {
    abstract val action: Action
    abstract val userInfo: UserInfo
    abstract val date: Instant
    abstract val amount: Double
    abstract val activityName: String
    abstract fun displayText(): String

    data class CreateDebtAction(
        val debtAction: DebtAction
    ) : TransactionActivity() {
        override val action: Action
            get() = debtAction
        override val userInfo: UserInfo
            get() = debtAction.userInfo
        override val date: Instant
            get() = debtAction.date
        override val amount: Double
            get() = debtAction.amount
        override val activityName: String
            get() = "Debt Request"

        override fun displayText() = "\"${debtAction.message}\""
    }

    data class CompletedSettleAction(
        val settleAction: SettleAction
    ) : TransactionActivity() {
        init {
            if (!settleAction.isCompleted)
                throw IllegalArgumentException("SettleAction not completed yet")
        }
        override val action: Action
            get() = settleAction
        override val userInfo: UserInfo
            get() = settleAction.userInfo
        override val date: Instant
            get() = settleAction.date
        override val amount: Double
            get() = settleAction.amount
        override val activityName: String
            get() = "Completed Settle"

        override fun displayText() =
            "${userInfo.user.displayName} completed a settlement for $amount"
    }
}
