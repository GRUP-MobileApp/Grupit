package com.grup.android.viewmodels.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grup.APIServer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableStateFlow(LoginResult())
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginEmailPassword(email: String, password: String) = viewModelScope.launch {
        try {
            APIServer.Login.emailAndPassword(email, password).also { successLoginResult() }
        } catch (e: Exception) {
            errorLoginResult(e)
        }
    }

    fun registerEmailPassword(email: String, password: String) = viewModelScope.launch {
        try {
            APIServer.Login.registerEmailAndPassword(email, password).also { successLoginResult() }
            // TODO: Put this in welcome slideshow
            APIServer.registerUser(email)
        } catch (e: Exception) {
            errorLoginResult(e)
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