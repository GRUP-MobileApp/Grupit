package com.grup.exceptions

internal class DoesNotExistException(
    override val message: String? = "Entity does not exist"
) : APIException(message)