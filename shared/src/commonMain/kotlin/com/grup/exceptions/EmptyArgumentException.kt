package com.grup.exceptions

internal class EmptyArgumentException(
    override val message: String? = "Empty argument"
) : APIException(message)