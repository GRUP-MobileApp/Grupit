package com.grup.exceptions

internal class InvalidTransactionRecordException(
    override val message: String? = "Bad TransactionRecord"
) : APIException(message)