package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.painter.Painter
import com.grup.other.AWS_IMAGES_BUCKET_NAME
import kotlinx.coroutines.flow.StateFlow

// Money
internal expect fun getCurrencySymbol(): String

internal expect fun Double.asMoneyAmount(): String

fun isoDate(date: String) = date.substring(5, 10)
fun isoFullDate(date: String) = date.substring(0, 10)

// Image
fun getProfilePictureURI(userId: String) =
    "https://$AWS_IMAGES_BUCKET_NAME.s3.amazonaws.com/pfp_$userId.png"

@Composable
internal expect fun profilePicturePainter(uri: String): Painter

@Composable
internal expect fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T>
