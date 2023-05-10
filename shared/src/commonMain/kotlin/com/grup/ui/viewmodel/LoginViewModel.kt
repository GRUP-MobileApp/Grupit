package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.APIServer
import com.grup.exceptions.login.LoginException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

internal class LoginViewModel : ScreenModel {
    sealed class LoginResult {
        object SuccessEmailPasswordLogin : LoginResult()
        object SuccessEmailPasswordRegister : LoginResult()
        object SuccessGoogleLogin : LoginResult()
        object PendingEmailPasswordLogin: LoginResult()
        object PendingEmailPasswordRegister : LoginResult()
        object PendingGoogleLogin: LoginResult()
        data class Error(val exception: Exception) : LoginResult()
        object None : LoginResult()
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.None)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginEmailPassword(email: String, password: String) = coroutineScope.launch {
        _loginResult.value = LoginResult.PendingEmailPasswordLogin
        try {
            injectApiServer(APIServer.loginEmailAndPassword(email, password))
            _loginResult.value = LoginResult.SuccessEmailPasswordLogin
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun registerEmailPassword(email: String, password: String) =
        coroutineScope.launch {
            _loginResult.value = LoginResult.PendingEmailPasswordRegister
            try {
                injectApiServer(APIServer.registerEmailAndPassword(email, password))
                _loginResult.value = LoginResult.SuccessEmailPasswordRegister
            } catch (e: LoginException) {
                _loginResult.value = LoginResult.Error(e)
            }
        }

    fun loginGoogleAccount(googleAccountToken: String) = coroutineScope.launch {
        _loginResult.value = LoginResult.PendingGoogleLogin
        try {
            injectApiServer(APIServer.loginGoogleAccountToken(googleAccountToken))
            _loginResult.value = LoginResult.SuccessGoogleLogin
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
