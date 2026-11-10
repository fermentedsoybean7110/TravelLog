package com.example.travellog

import android.app.Application
import com.example.travellog.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TravelLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TravelLogApplication)
            modules(appModule)
        }
    }
}
