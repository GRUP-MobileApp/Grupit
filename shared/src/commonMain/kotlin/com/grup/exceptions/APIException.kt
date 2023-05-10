package com.grup.exceptions

internal abstract class APIException(override val message: String?) : Exception(message)