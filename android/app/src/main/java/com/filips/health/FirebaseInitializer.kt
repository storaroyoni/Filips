package com.filips.health

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object FirebaseInitializer {
    fun initialize(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setProjectId("filips-health")
                    .setApplicationId("1:226987403199:android:bc13fc6f656019668d630a")
                    .setApiKey("AIzaSyAG4ld6W1jYl1nAyw3wKNtCvIsV_xPI_Y8")
                    .setStorageBucket("filips-health.firebasestorage.app")
                    .build()

                FirebaseApp.initializeApp(context, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val auth: FirebaseAuth by lazy { 
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    val firestore: FirebaseFirestore by lazy { 
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    val storage: FirebaseStorage by lazy { 
        try {
            FirebaseStorage.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
} 