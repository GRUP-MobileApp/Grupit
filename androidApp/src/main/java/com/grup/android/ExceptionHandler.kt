package com.grup.android

import android.content.Context
import android.widget.Toast
import com.grup.exceptions.APIException
import javax.security.auth.login.LoginException

class ExceptionHandler(
    private val context: Context
) : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(t: Thread, e: Throwable) {
        when(e) {
            is APIException -> {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
            is LoginException -> {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
            else -> {
                e.printStackTrace()
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                throw e
            }
        }
    }
}