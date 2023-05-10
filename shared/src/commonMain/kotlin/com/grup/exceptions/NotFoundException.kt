package com.grup.exceptions

internal class NotFoundException(
    override val message: String? = "Entity not found"
) : APIException(message)
