package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.APIServer
import com.grup.device.DeviceManager
import com.grup.exceptions.login.CancelledSignInException
import com.grup.exceptions.login.LoginException
import com.grup.exceptions.login.SignInException
import com.grup.platform.signin.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class LoginViewModel(private val isDebug: Boolean = false) : ScreenModel, KoinComponent {
    private val deviceManager: DeviceManager by inject()

    private var currentJob: Job? = null

    private fun launchJob(block: suspend CoroutineScope.() -> Unit) {
        if (currentJob?.isCompleted != false) {
            currentJob = screenModelScope.launch(block = block)
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
        data class PendingLogin(val authProvider: AuthManager.AuthProvider) : LoginResult()
        data class Error(val exception: Exception) : LoginResult()
        data object None : LoginResult()

        fun isPendingLoginAuthProvider(authProvider: AuthManager.AuthProvider) =
            (this is PendingLogin) && (this.authProvider == authProvider)
    }

    private val _loginResult = MutableStateFlow<LoginResult>(LoginResult.None)
    val loginResult: StateFlow<LoginResult> = _loginResult

    fun loginEmailPassword(
        email: String,
        password: String,
        onSuccessLogin: () -> Unit,
        onSuccessRegister: () -> Unit
    ) = launchJob {
        _loginResult.value = LoginResult.PendingLogin(AuthManager.AuthProvider.EmailPassword)
        try {
            APIServer.loginEmailAndPassword(email, password).let { apiServer ->
                injectApiServer(apiServer)
                if (apiServer.getMyUser(checkDB = true) != null) {
                    onSuccessLogin()
                } else {
                    onSuccessRegister()
                }
            }
        } catch (e: LoginException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun registerEmailPassword(
        email: String,
        password: String,
        onSuccessRegister: () -> Unit
    ) = launchJob {
        _loginResult.value = LoginResult.PendingLogin(
            AuthManager.AuthProvider.EmailPasswordRegister
        )
        try {
            APIServer.registerEmailAndPassword(email, password).let { apiServer ->
                injectApiServer(apiServer)
                onSuccessRegister()
            }
        } catch (e: CancelledSignInException) {
            _loginResult.value = LoginResult.None
        } catch (e: SignInException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun loginGoogleAccount(
        onSuccessLogin: () -> Unit,
        onSuccessRegister: (String?) -> Unit
    ) = launchJob {
        try {
            deviceManager.authManager.googleSignInManager?.signIn { token, name ->
                _loginResult.value = LoginResult.PendingLogin(AuthManager.AuthProvider.Google)
                APIServer.loginGoogleAccountToken(token).let { apiServer ->
                    injectApiServer(apiServer)
                    if (apiServer.getMyUser(checkDB = true) != null) {
                        onSuccessLogin()
                    } else {
                        onSuccessRegister(name)
                    }
                }
            }
        } catch (e: CancelledSignInException) {
            _loginResult.value = LoginResult.None
        } catch (e: SignInException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    fun loginAppleAccount(
        onSuccessLogin: () -> Unit,
        onSuccessRegister: (String?) -> Unit
    ) = launchJob {
        try {
            deviceManager.authManager.appleSignInManager?.signIn { token, name ->
                _loginResult.value = LoginResult.PendingLogin(AuthManager.AuthProvider.Apple)
                APIServer.loginAppleAccountToken(token).let { apiServer ->
                    injectApiServer(apiServer)
                    if (apiServer.getMyUser(checkDB = true) != null) {
                        onSuccessLogin()
                    } else {
                        onSuccessRegister(name)
                    }
                }
            }
        } catch (e: CancelledSignInException) {
            _loginResult.value = LoginResult.None
        } catch (e: SignInException) {
            _loginResult.value = LoginResult.Error(e)
        }
    }

    private fun injectApiServer(apiServer: APIServer) {
        LoggedInViewModel.apiServerInstance = apiServer
    }
}
