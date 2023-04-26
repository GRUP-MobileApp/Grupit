package com.grup.android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.grup.android.ExceptionHandler
import com.grup.android.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        setContentView(R.layout.activity_main)
    }
}
