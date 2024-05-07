package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.APIServer
import com.grup.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal abstract class LoggedInViewModel : KoinComponent, ScreenModel {
    private companion object {
        private const val STOP_TIMEOUT_MILLIS: Long = 5000
    }

    protected val apiServer: APIServer by inject()

    protected open val userObject: User
        get() = apiServer.user

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