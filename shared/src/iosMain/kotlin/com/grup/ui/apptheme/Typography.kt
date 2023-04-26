package com.grup.ui.apptheme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import io.ktor.utils.io.errors.*
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.NSMutableData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.getBytes

actual val proxima_nova: FontFamily = FontFamily.SansSerif
//    FontFamily(
//        Font(
//            "proxima_nova",
//            readFileToByteArray("proxima_nova.otf")
//        )
//    )

private fun readFileToByteArray(filePath: String): ByteArray {
    val fileUrl = NSURL.fileURLWithPath(filePath)
    val data = NSMutableData.dataWithContentsOfURL(fileUrl) ?: throw IOException("Unable to read file data")
    val readBuffer = ByteArray(data.length.toInt())

    memScoped {
        val bufferPointer = readBuffer.usePinned { it.addressOf(0) }
        data.getBytes(bufferPointer, data.length)
    }

    return readBuffer
}
