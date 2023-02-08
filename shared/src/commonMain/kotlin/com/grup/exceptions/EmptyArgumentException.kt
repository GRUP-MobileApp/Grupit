package com.grup.exceptions

class EmptyArgumentException(
    override val message: String? = "Empty argument"
) : APIException(message)