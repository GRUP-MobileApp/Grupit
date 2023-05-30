package com.grup.platform.image

import dev.icerock.moko.media.Bitmap
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.*
import platform.UIKit.*
import platform.posix.memcpy

internal actual fun cropCenterSquareImage(bitmap: Bitmap): ByteArray {
    val croppedUIImage = bitmap.image.CGImage?.let { cgImage ->
        val width = CGImageGetWidth(cgImage).toDouble()
        val height = CGImageGetHeight(cgImage).toDouble()

        val squareSize = minOf(width, height)
        val x = (width - squareSize) / 2
        val y = (height - squareSize) / 2

        val rect = CGRectMake(x, y, squareSize, squareSize)

        UIImage(CGImageCreateWithImageInRect(image = cgImage, rect = rect))
    } ?: throw NullPointerException("Null CGImage")

    val nsData = UIImagePNGRepresentation(croppedUIImage)
        ?: throw CharacterCodingException("Can't represent UIImage as PNG")

    return ByteArray(nsData.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), nsData.bytes, nsData.length)
        }
    }
}
