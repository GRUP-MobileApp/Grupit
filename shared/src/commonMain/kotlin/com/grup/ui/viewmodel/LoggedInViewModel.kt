package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.APIServer
import com.grup.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

internal abstract class LoggedInViewModel : KoinComponent, ScreenModel {
    companion object {
        private const val STOP_TIMEOUT_MILLIS: Long = 5000
    }

    protected val apiServer: APIServer by inject()

    protected val userObject: User
        get() = apiServer.user

    protected suspend fun closeApiServer() {
        apiServer.logOut()
        unloadKoinModules(
            module {
                single { apiServer }
            }
        )
    }

    protected fun <T> Flow<T>.asState() =
        this.let { flow ->
            runBlocking { flow.first() }.let { initialValue ->
                flow.stateIn(
                    coroutineScope,
                    SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialValue
                )
            }
        }

    protected fun <T> Flow<List<T>>.asInitialEmptyState() =
        this.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            emptyList()
        )

    protected fun <T> Flow<T>.asNotification(initialValue: T) =
        this.stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            initialValue
        )
}