@file:JvmName("UIAndroidKt")
package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import java.text.NumberFormat

internal actual fun getCurrencySymbol(): String =
    NumberFormat.getCurrencyInstance().currency?.symbol ?: "$"

internal actual fun Double.asMoneyAmount(): String =
    NumberFormat
        .getCurrencyInstance()
        .format(this)

@Composable
internal actual fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T> =
    this.collectAsStateWithLifecycle()
