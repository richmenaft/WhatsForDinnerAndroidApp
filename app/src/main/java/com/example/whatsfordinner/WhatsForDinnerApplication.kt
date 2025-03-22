package com.example.whatsfordinner

import android.app.Application
import com.example.whatsfordinner.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class WhatsForDinnerApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@WhatsForDinnerApplication)
            modules(appModule)
        }
    }
}