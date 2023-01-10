package com.grup.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.grup.APIServer
import com.grup.exceptions.login.UserObjectNotFoundException

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        try {
            APIServer.user
        } catch (e: UserObjectNotFoundException) {
            // TODO: Welcome slideshow
        }
        setContentView(R.layout.activity_main)
    }
}

