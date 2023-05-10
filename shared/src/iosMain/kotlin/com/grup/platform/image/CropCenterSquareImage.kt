package com.grup.platform.image

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.CoreGraphics.*
import platform.Foundation.*
import platform.UIKit.*
import platform.posix.memcpy

internal actual fun cropCenterSquareImage(byteArray: ByteArray): ByteArray {
    val nsData = UIImage(
        data = NSString.create(string = byteArray.decodeToString())
            .dataUsingEncoding(NSUTF8StringEncoding)!!
    ).CGImage.let { cgImage ->
        val width = CGImageGetWidth(cgImage).toDouble()
        val height = CGImageGetHeight(cgImage).toDouble()

        val squareSize = minOf(width, height)
        val x = (width - squareSize) / 2
        val y = (height - squareSize) / 2

        val rect = CGRectMake(x, y, squareSize, squareSize)

        UIImageJPEGRepresentation(
            image = UIImage
                .imageWithCGImage(CGImageCreateWithImageInRect(image = cgImage, rect = rect)),
            compressionQuality = 1.0
        )!!
    }

    return ByteArray(nsData.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), nsData.bytes, nsData.length)
        }
    }
}
