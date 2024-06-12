package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.APIServer
import com.grup.exceptions.login.NotLoggedInException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal abstract class LoggedInViewModel : ScreenModel {
    internal companion object {
        private const val STOP_TIMEOUT_MILLIS: Long = 5000

        var apiServerInstance: APIServer? = null
    }

    protected val apiServer: APIServer
        get() = apiServerInstance ?: throw NotLoggedInException()

    val userId: String by apiServer::userId

    private var currentJob: Job? = null

    protected fun launchJob(
        scope: CoroutineScope = screenModelScope,
        allowCancel: Boolean = false,
        block: suspend CoroutineScope.() -> Unit
    ) {
        if (currentJob?.isCompleted != false) {
            if (allowCancel) {
                currentJob?.cancel()
            }
            currentJob = scope.launch(block = block)
        }
    }

    protected fun <T> Flow<T>.asState() =
        runBlocking { this@asState.first() }.let { initialValue ->
            this.stateIn(
                screenModelScope,
                SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue
            )
        }

    protected fun <T> Flow<List<T>>.asInitialEmptyState() =
        this.stateIn(
            screenModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            emptyList()
        )

    protected fun <T> Flow<T>.asNotification(initialValue: T) =
        this.stateIn(
            screenModelScope,
            SharingStarted.Eagerly,
            initialValue
        )
}