package com.filips.health.data.model

data class HealthData(
    val steps: Int,
    val distance: Double, // in meters
    val calories: Double,
    val heartRate: Int
)