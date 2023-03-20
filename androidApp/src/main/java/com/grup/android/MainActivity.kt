package com.grup.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.grup.android.notifications.GroupInvitesViewModel
import com.grup.android.notifications.NotificationsViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        // Eagerly start getting notifications
        ViewModelProvider(this)[NotificationsViewModel::class.java]
        ViewModelProvider(this)[GroupInvitesViewModel::class.java]
        setContentView(R.layout.activity_main)
    }
}


