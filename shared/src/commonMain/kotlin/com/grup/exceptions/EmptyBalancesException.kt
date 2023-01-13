package com.grup.exceptions

class EmptyBalancesException(
    override val message: String? = "Balance is empty"
) : APIException(message)