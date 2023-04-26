package com.grup.other

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.*

actual fun getCurrencySymbol(): String =
    NSLocaleCurrencySymbol.toString()

actual fun Double.asMoneyAmount(): String =
    NSNumberFormatter().let {
        it.setNumberStyle(NSNumberFormatterCurrencyStyle)
        it.stringFromNumber(NSNumber(this))!!
    }

@Composable
actual fun profilePicturePainter(uri: String): Painter {
    return rememberVectorPainter(image = Icons.Default.Face)
}

@Composable
actual fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T> = this.collectAsState()
