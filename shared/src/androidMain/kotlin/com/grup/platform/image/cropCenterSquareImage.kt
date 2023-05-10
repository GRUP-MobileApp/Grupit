package com.grup.platform.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import kotlin.math.min

internal actual fun cropCenterSquareImage(byteArray: ByteArray): ByteArray {
    val croppedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        .let { androidBitmap ->
            val value = min(androidBitmap.height, androidBitmap.width)
            Bitmap.createBitmap(androidBitmap, 0, 0, value, value)
        }

    val stream = ByteArrayOutputStream()
    croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

    return stream.toByteArray().also { croppedBitmap.recycle() }
}