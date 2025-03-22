package com.filips.health.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HealthStats(
    val steps: String,
    val heartRate: String,
    val sleepHours: String
) : Parcelable

@Parcelize
data class ForumPost(
    val id: String,
    val title: String,
    val description: String,
    val isAnonymous: Boolean,
    val authorName: String,
    val timestamp: Long,
    val healthStats: HealthStats
) : Parcelable 