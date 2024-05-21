package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// Money
internal expect fun getCurrencySymbol(): String

internal expect fun Double.asMoneyAmount(): String

internal fun Double.asCurrencySymbolAndMoneyAmount(): Pair<String, String> =
    (if (this >= 0) 1 else 2).let { moneyAmountIndex ->
        Pair(
            this.asMoneyAmount().substring(0, moneyAmountIndex),
            this.asMoneyAmount().substring(moneyAmountIndex)
        )
    }

internal fun Double.asPureMoneyAmount(): String =
    this.asCurrencySymbolAndMoneyAmount().second

internal fun Double.roundTwoDecimalPlaces(): Double =
    (this * 100).toInt() / 100.0

// Date
private fun Instant.toLocalDT() = this.toLocalDateTime(TimeZone.currentSystemDefault())

fun isoDate(date: Instant) = date.toLocalDT().let { localDate ->
        localDate.month.name.substring(0, 3).let {
            it.first() + it.substring(1).lowercase()
        } + " ${localDate.dayOfMonth}"
    }
fun isoFullDate(date: Instant) = "${isoDate(date)} ${date.toLocalDT().year}"

fun isoTime(date: Instant): String {
    val localTime = date.toLocalDT().time

    val (hour, meridiem) = when(localTime.hour) {
        0 -> Pair(12, "AM")
        12 -> Pair(12, "PM")
        else -> Pair(localTime.hour % 12, if (localTime.hour < 12) "AM" else "PM")
    }
    val minute = localTime.minute.toString().padStart(2, '0')

    return "$hour:$minute $meridiem"
}

@Composable
internal expect fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T>
