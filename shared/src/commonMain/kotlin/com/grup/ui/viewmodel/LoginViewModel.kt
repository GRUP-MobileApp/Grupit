package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.APIServer
import com.grup.exceptions.login.LoginException
import com.grup.platform.signin.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

internal class LoginViewModel : ScreenModel {
    sealed class LoginResult {
        data class SuccessLogin(val authProvider: AuthManager.AuthProvider) : LoginResult()
        data class PendingLogin(val authProvider: AuthManager.AuthProvider) : LoginResult()
        data class Error(val exception: Exception) : LoginResult()
        object None : LoginResult()

        fun isSuccessLoginAuthProvider(authProvider: AuthManager.AuthProvider) =
            (this is SuccessLogin) && (this.authProvider == authProvider)

        fun isPendingLoginAuthProvider(authProvider: AuthManager.AuthProvider) =
            (this is PendingLogin) && (this.authProvider == authProvider)

        fun isSuccessOrPendingLoginAuthProvider(authProvider: AuthManager.AuthProvider) =
            isSuccessLoginAuthProvider(authProvider) || isPendingLoginAuthProvider(authProvider)
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.None)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginEmailPassword(email: String, password: String) = coroutineScope.launch {
        _loginResult.value = LoginResult.PendingLogin(AuthManager.AuthProvider.EmailPassword)
        try {
            injectApiServer(APIServer.loginEmailAndPassword(email, password))
            _loginResult.value = LoginResult.SuccessLogin(AuthManager.AuthProvider.EmailPassword)
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun registerEmailPassword(email: String, password: String) =
        coroutineScope.launch {
            _loginResult.value = LoginResult.PendingLogin(
                AuthManager.AuthProvider.EmailPasswordRegister
            )
            try {
                injectApiServer(APIServer.registerEmailAndPassword(email, password))
                _loginResult.value = LoginResult.SuccessLogin(
                    AuthManager.AuthProvider.EmailPasswordRegister
                )
            } catch (e: LoginException) {
                _loginResult.value = LoginResult.Error(e)
            }
        }

    fun loginGoogleAccount(googleAccountToken: String) = coroutineScope.launch {
        _loginResult.value = LoginResult.PendingLogin(AuthManager.AuthProvider.Google)
        try {
            injectApiServer(APIServer.loginGoogleAccountToken(googleAccountToken))
            _loginResult.value = LoginResult.SuccessLogin(AuthManager.AuthProvider.Google)
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
