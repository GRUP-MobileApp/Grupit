package com.grup.android

import android.app.Application
import com.grup.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}