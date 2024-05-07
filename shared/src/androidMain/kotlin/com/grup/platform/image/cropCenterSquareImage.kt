package com.grup.platform.image

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import java.io.ByteArrayOutputStream
import kotlin.math.min

private typealias MOKOBitmap = dev.icerock.moko.media.Bitmap

internal actual fun cropCenterSquareImage(bitmap: MOKOBitmap): ByteArray {
    val croppedBitmap = bitmap.platformBitmap.let {  androidBitmap ->
        val squareSize = min(androidBitmap.height, androidBitmap.width)

        ThumbnailUtils.extractThumbnail(androidBitmap, squareSize, squareSize)
    }

    val stream = ByteArrayOutputStream()
    croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    return stream.toByteArray()
}
