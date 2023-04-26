package com.grup.ui.viewmodel

import com.grup.APIServer
import com.grup.exceptions.login.LoginException
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class LoginViewModel : KMMViewModel() {
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

    fun loginEmailPassword(email: String, password: String) = viewModelScope.coroutineScope.launch {
        _loginResult.value = LoginResult.PendingLogin
        try {
            injectApiServer(APIServer.loginEmailAndPassword(email, password))
            _loginResult.value = LoginResult.Success
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun registerEmailPassword(email: String, password: String) =
        viewModelScope.coroutineScope.launch {
            _loginResult.value = LoginResult.PendingRegister
            try {
                injectApiServer(APIServer.registerEmailAndPassword(email, password))
                _loginResult.value = LoginResult.Success
            } catch (e: LoginException) {
                _loginResult.value = LoginResult.Error(e)
            }
        }

    fun loginGoogleAccount(googleAccountToken: String) = viewModelScope.coroutineScope.launch {
        _loginResult.value = LoginResult.PendingGoogleLogin
        try {
            injectApiServer(APIServer.loginGoogleAccountToken(googleAccountToken))
            _loginResult.value = LoginResult.Success
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    private fun injectApiServer(apiServer: APIServer) {
        loadKoinModules(
            module {
                single { apiServer }
            }
        )
    }
}
