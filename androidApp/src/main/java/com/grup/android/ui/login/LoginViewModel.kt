package com.grup.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grup.APIServer
import com.grup.exceptions.login.LoginException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableStateFlow(LoginResult())
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginEmailPassword(email: String, password: String) = viewModelScope.launch {
        pendingLoginResult()
        try {
            APIServer.Login.emailAndPassword(email, password).also { successLoginResult() }
        } catch (e: LoginException) {
            errorLoginResult(e)
        }
    }

    fun registerEmailPassword(email: String, password: String) = viewModelScope.launch {
        pendingLoginResult()
        try {
            APIServer.Login.registerEmailAndPassword(email, password).also { successLoginResult() }
            // TODO: Put this in welcome slideshow
            APIServer.registerUser(email)
        } catch (e: LoginException) {
            errorLoginResult(e)
        }
    }

    private fun pendingLoginResult() {
        _loginResult.value = LoginResult().apply {
            this.status = LoginResult.LoginStatus.PENDING
        }
    }

    private fun successLoginResult() {
        _loginResult.value = LoginResult().apply {
            this.status = LoginResult.LoginStatus.SUCCESS
        }
    }

    private fun errorLoginResult(e: Exception) {
        _loginResult.value = LoginResult().apply {
            this.status = LoginResult.LoginStatus.ERROR
            this.error = e
        }
    }
}