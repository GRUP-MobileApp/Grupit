package com.grup.models

import kotlinx.datetime.Instant

sealed class Action : BaseEntity() {
    abstract val userInfo: UserInfo
    abstract val transactionRecords: List<TransactionRecord>
    abstract val date: Instant

    abstract val amount: Double
}
