package com.grup.android

import android.content.Context
import android.widget.Toast

class ExceptionHandler(
    private val context: Context
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace()
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
}