package com.filips.health

import android.app.Application
import com.google.firebase.FirebaseApp

class HealthApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}