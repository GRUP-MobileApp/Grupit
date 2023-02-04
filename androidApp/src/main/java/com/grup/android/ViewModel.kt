package com.grup.android

import androidx.lifecycle.viewModelScope
import com.grup.APIServer
import com.grup.models.Group
import com.grup.models.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

abstract class ViewModel : androidx.lifecycle.ViewModel() {
    companion object {
        private const val STOP_TIMEOUT_MILLIS: Long = 5000
    }

    protected val userObject: User
        get() = APIServer.user

    protected fun <T> Flow<T>.asState() =
        this.let { flow ->
            runBlocking { flow.first() }.let { initialList ->
                flow.stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                    initialList
                )
            }
        }

    protected fun <T> Flow<List<T>>.asInitialEmptyState() =
        this.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            emptyList()
        )
}