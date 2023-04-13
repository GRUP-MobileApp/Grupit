package com.grup.android.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.grup.APIServer
import com.grup.android.LoggedInViewModel
import com.grup.exceptions.login.LoginException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    sealed class LoginResult {
        object Success : LoginResult()
        object PendingLogin: LoginResult()
        object PendingRegister : LoginResult()
        object PendingGoogleLogin: LoginResult()
        data class Error(val exception: Exception) : LoginResult()
        object None : LoginResult()
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.None)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginEmailPassword(email: String, password: String) = viewModelScope.launch {
        _loginResult.value = LoginResult.PendingLogin
        try {
            LoggedInViewModel.injectApiServer(
                APIServer.Login.loginEmailAndPassword(email, password)
            )
            _loginResult.value = LoginResult.Success
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun registerEmailPassword(email: String, password: String) = viewModelScope.launch {
        _loginResult.value = LoginResult.PendingRegister
        try {
            LoggedInViewModel.injectApiServer(
                APIServer.Login.registerEmailAndPassword(email, password)
            )
            _loginResult.value = LoginResult.Success
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun loginGoogleAccount(task: Task<GoogleSignInAccount>) = viewModelScope.launch {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            val token: String = account?.idToken!!

            _loginResult.value = LoginResult.PendingGoogleLogin
            try {
                LoggedInViewModel.injectApiServer(
                    APIServer.Login.loginGoogleAccountToken(token)
                )
                _loginResult.value = LoginResult.Success
            } catch (e: LoginException) {
                _loginResult.value = LoginResult.Error(e)
            }
        }
    }
}
