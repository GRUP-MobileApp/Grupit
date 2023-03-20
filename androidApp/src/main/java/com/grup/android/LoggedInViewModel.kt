package com.grup.android

import androidx.lifecycle.viewModelScope
import com.grup.APIServer
import com.grup.models.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock

abstract class LoggedInViewModel : androidx.lifecycle.ViewModel() {
    companion object {
        private const val STOP_TIMEOUT_MILLIS: Long = 5000
        private var loggedInApiServer: APIServer? = null

        fun injectApiServer(apiServer: APIServer) {
            loggedInApiServer = apiServer
        }
    }

    protected val apiServer: APIServer
        get() = loggedInApiServer!!

    protected val userObject: User
        get() = apiServer.user

    protected suspend fun closeApiServer() {
        apiServer.logOut()
        loggedInApiServer = null
    }

    protected fun <T> Flow<T>.asState() =
        this.let { flow ->
            runBlocking { flow.first() }.let { initialValue ->
                flow.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue
                )
            }
        }

    protected fun <T> Flow<List<T>>.asInitialEmptyState() =
        this.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            emptyList()
        )

    protected fun <T> Flow<T>.asNotification(initialValue: T) =
        this.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            initialValue
        )
}