package com.grup.exceptions

abstract class APIException(override val message: String?) : Exception(message)