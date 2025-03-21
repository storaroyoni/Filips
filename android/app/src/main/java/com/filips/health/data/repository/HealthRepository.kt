package com.filips.health.data.repository

import android.app.Activity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepository @Inject constructor() {

    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .build()

    private val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    suspend fun getLatestHealthData(activity: Activity) {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - TimeUnit.DAYS.toMillis(1) // Last 24 hours

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_STEP_COUNT_DELTA)
            .read(DataType.TYPE_DISTANCE_DELTA)
            .read(DataType.TYPE_CALORIES_EXPENDED)
            .read(DataType.TYPE_HEART_RATE_BPM)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        if (hasGoogleFitPermissions(activity)) {
            Fitness.getHistoryClient(activity, GoogleSignIn.getLastSignedInAccount(activity)!!)
                .readData(readRequest)
                .addOnSuccessListener { response ->

                }
                .addOnFailureListener { e ->

                }
        }
    }

    suspend fun syncWithGoogleFit(userId: String) {

    }

    fun hasGoogleFitPermissions(activity: Activity): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        return account != null && GoogleSignIn.hasPermissions(account, fitnessOptions)
    }

    fun getGoogleFitPermissionIntent(activity: Activity) =
        GoogleSignIn.getClient(activity, signInOptions).signInIntent
}