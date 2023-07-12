package com.grup.ui.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.*

internal actual fun getCurrencySymbol(): String =
    // TODO: Use NSLocaleCurrencyCode
    "$"

internal actual fun Double.asMoneyAmount(): String =
    NSNumberFormatter().let {
        it.setNumberStyle(NSNumberFormatterCurrencyStyle)
        it.stringFromNumber(NSNumber(this))!!
    }

@Composable
internal actual fun profilePicturePainter(uri: String): Painter {
    return rememberVectorPainter(image = Icons.Default.Face)
}

@Composable
internal actual fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T> =
    this.collectAsState()

internal actual fun String.parseMoneyAmount(): Double? =
    NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterDecimalStyle
        locale = NSLocale.currentLocale
    }.numberFromString(this)?.doubleValue
