@file:JvmName("UIAndroidKt")
package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import kotlinx.coroutines.flow.StateFlow
import java.text.NumberFormat

internal actual fun getCurrencySymbol(): String =
    NumberFormat.getCurrencyInstance().currency!!.symbol

internal actual fun Double.asMoneyAmount(): String =
    NumberFormat
        .getCurrencyInstance()
        .format(this)

@Composable
internal actual fun profilePicturePainter(uri: String): Painter {
    val context = LocalContext.current
    val imageRequest: ImageRequest =
        ImageRequest.Builder(context)
            .data(uri)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .allowHardware(true)
            .diskCacheKey(uri)
            .memoryCacheKey(uri)
            .transformations(CircleCropTransformation())
            .build()
    return rememberAsyncImagePainter(
        model = imageRequest,
        imageLoader = context.imageLoader
    )
}

@Composable
internal actual fun <T> StateFlow<T>.collectAsStateWithLifecycle(): State<T> =
    this.collectAsStateWithLifecycle()
