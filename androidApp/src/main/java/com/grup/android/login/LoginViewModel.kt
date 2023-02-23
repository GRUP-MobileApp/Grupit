package com.grup.android.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.grup.APIServer
import com.grup.exceptions.login.LoginException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    sealed class LoginResult {
        object Success : LoginResult()
        object PendingLogin: LoginResult()
        object PendingRegister : LoginResult()
        data class Error(val exception: Exception) : LoginResult()
        object None : LoginResult()
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.None)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginEmailPassword(email: String, password: String) {
        _loginResult.value = LoginResult.PendingLogin
        viewModelScope.launch {
            try {
                APIServer.Login.emailAndPassword(email, password)
                _loginResult.value = LoginResult.Success
            } catch (e: LoginException) {
                _loginResult.value = LoginResult.Error(e)
            }
        }
    }

    fun registerEmailPassword(email: String, password: String) {
        _loginResult.value = LoginResult.PendingRegister
        viewModelScope.launch {
            try {
                APIServer.Login.registerEmailAndPassword(email, password)
                _loginResult.value = LoginResult.Success
            } catch (e: LoginException) {
                _loginResult.value = LoginResult.Error(e)
            }
        }
    }

    fun handleSignInResult(googleSignInAccount: GoogleSignInAccount) {
        try {

            // Get user details
            val name = googleSignInAccount?.displayName
            val email = googleSignInAccount?.email
            val idToken = googleSignInAccount?.idToken

            // login and change active user code here

        } catch (e: ApiException) {
            // ...
        }
    }

}
