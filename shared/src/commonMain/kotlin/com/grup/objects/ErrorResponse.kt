package com.grup.objects

data class ErrorResponse(val message: String) {
    companion object {
        val NOT_FOUND_RESPONSE = ErrorResponse("User/Group not found")
        val USER_ALREADY_EXISTS = ErrorResponse("User already exists")
        val BAD_REQUEST_RESPONSE = ErrorResponse("Invalid request")
        val BAD_TRANSACTION_RECORD = ErrorResponse("Transaction record conflict")
    }
}
