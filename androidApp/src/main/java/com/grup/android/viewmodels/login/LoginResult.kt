package com.grup.android.viewmodels.login

class LoginResult {
    enum class LoginStatus {
        SUCCESS,
        ERROR
    }
    var status: LoginStatus? = null
    var error: Exception? = null
}