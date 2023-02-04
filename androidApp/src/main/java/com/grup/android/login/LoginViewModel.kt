package com.grup.android.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grup.APIServer
import com.grup.exceptions.login.LoginException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    sealed class LoginResult {
        object Success : LoginResult()
        object Pending : LoginResult()
        data class Error(val exception: Exception) : LoginResult()
        object Empty : LoginResult()
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.Empty)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginEmailPassword(email: String, password: String) = viewModelScope.launch {
        _loginResult.value = LoginResult.Pending
        try {
            APIServer.Login.emailAndPassword(email, password).also {
                _loginResult.value = LoginResult.Success
            }
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun registerEmailPassword(email: String, password: String) = viewModelScope.launch {
        _loginResult.value = LoginResult.Pending
        try {
            APIServer.Login.registerEmailAndPassword(email, password).also {
                _loginResult.value = LoginResult.Success
            }
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }
}