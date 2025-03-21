package com.example.googlefitapp

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Task
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class GoogleFitHelper(private val context: Context) {

    companion object {
        const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1001
        private const val TAG = "GoogleFitHelper"
    }

    // Data class to represent step data by hour
    data class HourlyStepData(
        val hour: Int,
        val steps: Int
    )

    // Data class to represent step data by date
    data class DailyStepData(
        val date: String,
        val totalSteps: Int,
        val hourlySteps: List<HourlyStepData>
    )

    val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.AGGREGATE_HEART_RATE_SUMMARY, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
        .build()

    fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(context) != null
    }

    fun hasPermissions(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null && GoogleSignIn.hasPermissions(account, fitnessOptions)
    }

    fun getSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso).signInIntent
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, "Sign-in successful: ${account.email}")
            onSuccess()
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in failed", e)
            onFailure(e)
        }
    }

    data class FitnessData(
        val steps: Int = 0,
        val distance: Float = 0f,
        val calories: Float = 0f,
        val heartRate: Float = 0f,
        val weight: Float = 0f,
        val height: Float = 0f,
        val sleepDuration: Long = 0L
    )

    // New function to fetch detailed step count data by date and hour
    fun fetchDetailedStepData(
        numberOfDays: Int = 7,
        onSuccess: (List<DailyStepData>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account == null) {
            onFailure(Exception("No signed-in account"))
            return
        }

        if (!hasPermissions()) {
            onFailure(Exception("Google Fit permissions not granted"))
            return
        }

        try {
        val fitnessClient = Fitness.getHistoryClient(context, account)

            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            
            // Set time to end of day
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endTimeAdjusted = calendar.timeInMillis
            
            // Go back to the start date
            calendar.add(Calendar.DAY_OF_YEAR, -(numberOfDays - 1))
            // Set time to start of day
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startTime = calendar.timeInMillis

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            
            Log.d(TAG, "Range Start: ${dateFormat.format(Date(startTime))}")
            Log.d(TAG, "Range End: ${dateFormat.format(Date(endTimeAdjusted))}")

            // Request for daily step totals
            val dailyRequest = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTimeAdjusted, TimeUnit.MILLISECONDS)
                .build()

            // Request for hourly step data
            val hourlyRequest = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.HOURS)
                .setTimeRange(startTime, endTimeAdjusted, TimeUnit.MILLISECONDS)
                .build()

            // First, get daily step totals
            fitnessClient.readData(dailyRequest)
                .addOnSuccessListener { dailyResponse ->
                    // Create map to store daily data by date string
                    val dailyDataMap = mutableMapOf<String, DailyStepData>()
                    
                    // Process daily buckets
                    for (bucket in dailyResponse.buckets) {
                        val bucketDate = Date(bucket.getStartTime(TimeUnit.MILLISECONDS))
                        val dateString = dateFormat.format(bucketDate)
                        var dailySteps = 0
                        
                        for (dataSet in bucket.dataSets) {
                            for (dataPoint in dataSet.dataPoints) {
                                dailySteps += dataPoint.getValue(Field.FIELD_STEPS).asInt()
                            }
                        }
                        
                        // Initialize DailyStepData with empty hourly data
                        dailyDataMap[dateString] = DailyStepData(
                            date = dateString,
                            totalSteps = dailySteps,
                            hourlySteps = emptyList()
                        )
                    }
                    
                    // Now get hourly data
                    fitnessClient.readData(hourlyRequest)
                        .addOnSuccessListener { hourlyResponse ->
                            // Process hourly buckets
                            for (bucket in hourlyResponse.buckets) {
                                val bucketDate = Date(bucket.getStartTime(TimeUnit.MILLISECONDS))
                                val calendar = Calendar.getInstance().apply {
                                    time = bucketDate
                                }
                                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                                val dateString = dateFormat.format(bucketDate)
                                
                                var hourlySteps = 0
                                
                                for (dataSet in bucket.dataSets) {
                                    for (dataPoint in dataSet.dataPoints) {
                                        hourlySteps += dataPoint.getValue(Field.FIELD_STEPS).asInt()
                                    }
                                }
                                
                                // Skip hours with zero steps to keep data concise
                                if (hourlySteps > 0) {
                                    // Get existing daily data or create new
                                    val dailyData = dailyDataMap[dateString] ?: DailyStepData(
                                        date = dateString,
                                        totalSteps = 0,
                                        hourlySteps = emptyList()
                                    )
                                    
                                    // Add hourly data
                                    val updatedHourlySteps = dailyData.hourlySteps.toMutableList()
                                    updatedHourlySteps.add(HourlyStepData(hour, hourlySteps))
                                    
                                    // Sort by hour
                                    val sortedHourlySteps = updatedHourlySteps.sortedBy { it.hour }
                                    
                                    // Update the map
                                    dailyDataMap[dateString] = dailyData.copy(
                                        hourlySteps = sortedHourlySteps
                                    )
                                }
                            }
                            
                            // Convert map to list sorted by date (most recent first)
                            val dailyStepDataList = dailyDataMap.values.sortedByDescending { it.date }
                            
                            Log.d(TAG, "Daily step data: $dailyStepDataList")
                            onSuccess(dailyStepDataList)
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Failed to read hourly step data", e)
                            onFailure(e)
                        }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to read daily step data", e)
                    onFailure(e)
                }
            
        } catch (e: Exception) {
            Log.e(TAG, "Exception while reading step data", e)
            onFailure(e)
        }
    }

    fun fetchAllFitnessData(onSuccess: (FitnessData) -> Unit, onFailure: (Exception) -> Unit) {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account == null) {
            onFailure(Exception("No signed-in account"))
            return
        }

        if (!hasPermissions()) {
            onFailure(Exception("Google Fit permissions not granted"))
            return
        }

        try {
            val fitnessClient = Fitness.getHistoryClient(context, account)

            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            val startTime = calendar.timeInMillis

            val dateFormat = DateFormat.getDateInstance()
            Log.d(TAG, "Range Start: ${dateFormat.format(Date(startTime))}")
            Log.d(TAG, "Range End: ${dateFormat.format(Date(endTime))}")

        val request = DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_HEART_RATE_BPM)
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_HEIGHT)
                .read(DataType.TYPE_SLEEP_SEGMENT)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

            Log.d(TAG, "Reading fitness data from $startTime to $endTime")

        fitnessClient.readData(request)
            .addOnSuccessListener { response ->
                var totalSteps = 0
                    var totalDistance = 0f
                    var totalCalories = 0f
                    var avgHeartRate = 0f
                    var weight = 0f
                    var height = 0f
                    var sleepDuration = 0L
                    var heartRateCount = 0

                for (bucket in response.buckets) {
                        Log.d(TAG, "Bucket: ${bucket.activity} from ${dateFormat.format(bucket.getStartTime(TimeUnit.MILLISECONDS))} to ${dateFormat.format(bucket.getEndTime(TimeUnit.MILLISECONDS))}")

                    for (dataSet in bucket.dataSets) {
                            processDataSet(
                                dataSet,
                                onStepCount = { totalSteps += it },
                                onDistance = { totalDistance += it },
                                onCalories = { totalCalories += it },
                                onHeartRate = {
                                    avgHeartRate += it
                                    heartRateCount++
                                }
                            )
                        }
                    }

                    for (dataSet in response.dataSets) {
                        when (dataSet.dataType) {
                            DataType.TYPE_WEIGHT -> {
                                if (dataSet.dataPoints.isNotEmpty()) {
                                    val latestPoint = dataSet.dataPoints.maxByOrNull { it.getEndTime(TimeUnit.MILLISECONDS) }
                                    latestPoint?.let {
                                        weight = it.getValue(Field.FIELD_WEIGHT).asFloat()
                                    }
                                }
                            }
                            DataType.TYPE_HEIGHT -> {
                                if (dataSet.dataPoints.isNotEmpty()) {
                                    val latestPoint = dataSet.dataPoints.maxByOrNull { it.getEndTime(TimeUnit.MILLISECONDS) }
                                    latestPoint?.let {
                                        height = it.getValue(Field.FIELD_HEIGHT).asFloat()
                                    }
                                }
                            }
                            DataType.TYPE_SLEEP_SEGMENT -> {
                        for (dataPoint in dataSet.dataPoints) {
                                    val start = dataPoint.getStartTime(TimeUnit.MILLISECONDS)
                                    val end = dataPoint.getEndTime(TimeUnit.MILLISECONDS)
                                    sleepDuration += (end - start)
                                }
                            }
                        }
                    }

                    if (heartRateCount > 0) {
                        avgHeartRate /= heartRateCount
                    }

                    val fitnessData = FitnessData(
                        steps = totalSteps,
                        distance = totalDistance,
                        calories = totalCalories,
                        heartRate = avgHeartRate,
                        weight = weight,
                        height = height,
                        sleepDuration = sleepDuration
                    )

                    Log.d(TAG, "Fitness data: $fitnessData")
                    onSuccess(fitnessData)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to read fitness data", e)
                    onFailure(e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception while reading fitness data", e)
                onFailure(e)
            }
    }
    
    private fun processDataSet(
        dataSet: DataSet,
        onStepCount: (Int) -> Unit = {},
        onDistance: (Float) -> Unit = {},
        onCalories: (Float) -> Unit = {},
        onHeartRate: (Float) -> Unit = {}
    ) {
        Log.d(TAG, "Data set: ${dataSet.dataType.name}")
        
        for (dataPoint in dataSet.dataPoints) {
            when (dataSet.dataType) {
                DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA -> {
                    val steps = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                    onStepCount(steps)
                    Log.d(TAG, "Steps: $steps")
                }
                DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA -> {
                    val distance = dataPoint.getValue(Field.FIELD_DISTANCE).asFloat()
                    onDistance(distance)
                    Log.d(TAG, "Distance: $distance")
                }
                DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED -> {
                    val calories = dataPoint.getValue(Field.FIELD_CALORIES).asFloat()
                    onCalories(calories)
                    Log.d(TAG, "Calories: $calories")
                }
                DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY -> {
                    val heartRate = dataPoint.getValue(Field.FIELD_AVERAGE).asFloat()
                    onHeartRate(heartRate)
                    Log.d(TAG, "Heart Rate: $heartRate")
                }
            }
        }
    }
    
    fun fetchStepCount(onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        fetchAllFitnessData(
            onSuccess = { fitnessData -> onSuccess(fitnessData.steps) },
            onFailure = onFailure
        )
    }
}