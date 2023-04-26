package com.grup.other

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.painter.Painter
import com.grup.APIServer
import kotlinx.coroutines.flow.StateFlow

// Money
expect fun getCurrencySymbol(): String

expect fun Double.asMoneyAmount(): String

fun isoDate(date: String) = date.substring(5, 10)
fun isoFullDate(date: String) = date.substring(0, 10)

// Image
fun getProfilePictureURI(userId: String) = APIServer.Images.getProfilePictureURI(userId)

@Composable expect fun profilePicturePainter(uri: String): Painter

@Composable expect fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T>
