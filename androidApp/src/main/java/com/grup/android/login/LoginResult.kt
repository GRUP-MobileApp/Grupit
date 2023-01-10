package com.grup.android.login

class LoginResult {
    enum class LoginStatus {
        PENDING,
        SUCCESS,
        ERROR,
    }
    var status: LoginStatus? = null
    var error: Exception? = null
}