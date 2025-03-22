package com.filips.health

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FilipsHealthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseInitializer.initialize(this)
    }
} 