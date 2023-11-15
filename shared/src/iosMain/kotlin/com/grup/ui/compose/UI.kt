package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

internal actual fun getCurrencySymbol(): String =
    // TODO: Use NSLocaleCurrencyCode
    "$"

internal actual fun Double.asMoneyAmount(): String =
    NSNumberFormatter().let {
        it.setNumberStyle(NSNumberFormatterCurrencyStyle)
        it.stringFromNumber(NSNumber(this))!!
    }

@Composable
internal actual fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T> =
    this.collectAsState()
