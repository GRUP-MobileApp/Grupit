package com.grup.exceptions

class InvalidTransactionRecordException(
    override val message: String? = "Bad TransactionRecord"
) : APIException(message)