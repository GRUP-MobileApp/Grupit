package com.grup.android

class ExceptionHandler : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace()
    }
}