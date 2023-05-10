package com.grup.exceptions

internal class NotCreatedException(
    override val message: String? = "Error creating model"
) : APIException(message)