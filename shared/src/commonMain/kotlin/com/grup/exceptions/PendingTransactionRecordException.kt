package com.grup.exceptions

class PendingTransactionRecordException(
    override val message: String? = "Transaction activity has not yet been accepted"
) : APIException(message)