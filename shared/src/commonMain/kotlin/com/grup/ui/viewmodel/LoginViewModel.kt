package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.APIServer
import com.grup.device.DeviceManager
import com.grup.exceptions.APIException
import com.grup.exceptions.login.LoginException
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.platform.signin.AuthManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

internal class LoginViewModel(private val isDebug: Boolean = false) : ScreenModel, KoinComponent {
    private val deviceManager: DeviceManager by inject()

    private var currentJob: Job? = null

    private fun launchJob(block: suspend () -> Unit) {
        if (currentJob?.isCompleted != false) {
            currentJob = screenModelScope.launch {
                block()
            }
        }
    }

    fun allowAuthProvider(authProvider: AuthManager.AuthProvider): Boolean = when(authProvider) {
        AuthManager.AuthProvider.Apple -> deviceManager.authManager.appleSignInManager != null
        AuthManager.AuthProvider.Google -> deviceManager.authManager.googleSignInManager != null
        AuthManager.AuthProvider.EmailPassword -> isDebug
        AuthManager.AuthProvider.EmailPasswordRegister -> isDebug
        AuthManager.AuthProvider.None -> false
    }

    sealed class LoginResult {
        data class SuccessLogin(val authProvider: AuthManager.AuthProvider) : LoginResult()
        data class SuccessLoginWelcomeSlideshow(
            val authProvider: AuthManager.AuthProvider
        ) : LoginResult()
        data class PendingLogin(val authProvider: AuthManager.AuthProvider) : LoginResult()
        data class Error(val exception: Exception) : LoginResult()
        data object None : LoginResult()

        private fun isSuccessLoginAuthProvider(authProvider: AuthManager.AuthProvider) =
            (this is SuccessLogin) && (this.authProvider == authProvider)

        private fun isPendingLoginAuthProvider(authProvider: AuthManager.AuthProvider) =
            (this is PendingLogin) && (this.authProvider == authProvider)

        fun isSuccessOrPendingLoginAuthProvider(authProvider: AuthManager.AuthProvider) =
            isSuccessLoginAuthProvider(authProvider) || isPendingLoginAuthProvider(authProvider)
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.None)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginEmailPassword(email: String, password: String) = launchJob {
        _loginResult.value = LoginResult.PendingLogin(AuthManager.AuthProvider.EmailPassword)
        try {
            APIServer.loginEmailAndPassword(email, password).let { apiServer ->
                injectApiServer(apiServer)
                try {
                    apiServer.user
                    _loginResult.value = LoginResult.SuccessLogin(
                        AuthManager.AuthProvider.EmailPassword
                    )
                } catch (e: UserObjectNotFoundException) {
                    _loginResult.value = LoginResult.SuccessLoginWelcomeSlideshow(
                        AuthManager.AuthProvider.EmailPassword
                    )
                }
            }
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun registerEmailPassword(email: String, password: String) = launchJob {
            _loginResult.value = LoginResult.PendingLogin(
                AuthManager.AuthProvider.EmailPasswordRegister
            )
            try {
                APIServer.registerEmailAndPassword(email, password).let { apiServer ->
                    injectApiServer(apiServer)
                    try {
                        apiServer.user
                        _loginResult.value = LoginResult.SuccessLogin(
                            AuthManager.AuthProvider.EmailPasswordRegister
                        )
                    } catch (e: UserObjectNotFoundException) {
                        _loginResult.value = LoginResult.SuccessLoginWelcomeSlideshow(
                            AuthManager.AuthProvider.EmailPasswordRegister
                        )
                    }
                }
            } catch (e: LoginException) {
                _loginResult.value = LoginResult.Error(e)
            }
        }

    fun loginGoogleAccount() = launchJob {
        try {
            deviceManager.authManager.googleSignInManager?.signIn { token ->
                _loginResult.value = LoginResult.PendingLogin(AuthManager.AuthProvider.Google)
                screenModelScope.launch {
                    APIServer.loginGoogleAccountToken(token).let { apiServer ->
                        injectApiServer(apiServer)
                        try {
                            apiServer.user
                            _loginResult.value =
                                LoginResult.SuccessLogin(AuthManager.AuthProvider.Google)
                        } catch (e: UserObjectNotFoundException) {
                            _loginResult.value = LoginResult.SuccessLoginWelcomeSlideshow(
                                AuthManager.AuthProvider.Google
                            )
                        }
                    }
                }
            }
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.None
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
