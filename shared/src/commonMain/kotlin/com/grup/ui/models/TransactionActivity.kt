package com.grup.ui.models

import com.grup.models.*

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
            get() = debtAction.userInfo
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
            get() = settleAction.userInfo
        override val date: String
            get() = settleAction.date
        override val amount: Double
            get() = settleAction.totalAmount
        override val activityName: String
            get() = if (settleAction.transactionRecords.any { !it.isAccepted }) "Settle"
                    else "Completed Settle"

        override fun displayText() = "created a new settlement"
    }
}
