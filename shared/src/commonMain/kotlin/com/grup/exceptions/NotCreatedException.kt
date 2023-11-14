package com.grup.exceptions

class NotCreatedException(
    override val message: String? = "Error creating model"
) : APIException(message)