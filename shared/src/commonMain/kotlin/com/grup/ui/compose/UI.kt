package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.painter.Painter
import com.grup.other.AWS_IMAGES_BUCKET_NAME
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDateTime

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

// Date

fun isoDate(date: String) = LocalDateTime.parse(date).date.let {  localDate ->
    "${localDate.monthNumber}-${localDate.dayOfMonth}"
}
fun isoFullDate(date: String) = "${isoDate(date)}-${LocalDateTime.parse(date).year}"

fun isoTime(date: String) = LocalDateTime.parse(date).time.let { localTime ->
    localTime.hour.let { hour ->
        localTime.minute.let { minute ->
            "${hour % 12}:${if (minute < 10) 0 else ""}${minute} " +
                    if (hour / 12 > 0) "AM" else "PM"
        }
    }
}

// Image
fun getProfilePictureURI(userId: String) =
    "https://$AWS_IMAGES_BUCKET_NAME.s3.amazonaws.com/pfp_$userId.png"

@Composable
internal expect fun profilePicturePainter(uri: String): Painter

@Composable
internal expect fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T>
