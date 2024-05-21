package com.grup.exceptions

class AccountDeletionError(
    override val message: String? = "Account deletion error"
) : APIException(message)