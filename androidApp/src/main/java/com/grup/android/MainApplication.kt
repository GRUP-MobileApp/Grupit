package com.grup.android

import GOOGLE_WEB_CLIENT_ID
import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.grup.repositories.PreferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            androidLogger()

            loadKoinModules(
                module {
                    single { PreferencesDataStore(get()) }
                    single {
                        GoogleSignIn.getClient(
                        this@MainApplication,
                        GoogleSignInOptions
                            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                            .build()
                        )
                    }
                }
            )
        }
    }
}