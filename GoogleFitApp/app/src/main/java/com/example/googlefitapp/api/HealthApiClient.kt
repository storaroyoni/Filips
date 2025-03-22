package com.example.googlefitapp.api

import android.content.Context
import android.telecom.Call
import android.util.Log
import androidx.contentpager.content.Query
import com.example.googlefitapp.GoogleFitHelper
import com.google.android.gms.common.api.Response
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Models matching backend entities
data class User(
    val id: Int? = null,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class Device(
    val deviceId: Int? = null,
    val deviceType: DeviceType,
    val deviceName: String,
    val registrationDate: String? = null,
    val lastSyncDate: String? = null,
    val user: User? = null
)

enum class DeviceType {
    SMARTWATCH,
    HEALTH_HUB
}

enum class DataTypes {
    STEPS,
    HEARTBEAT,
    BLOOD_PRESSURE,
    AIR_QUALITY,
    ROOM_TEMPERATURE,
    C02
}

data class HealthData(
    val id: Int? = null,
    val deviceId: Int,
    val measuredAt: String,
    val dataType: DataTypes,
    val valueNumeric: Int,
    val valueText: String? = null,
    val unit: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

// Retrofit interface
interface HealthApiService {
    // User endpoints
    @POST("api/users/register")
    fun registerUser(@Body user: User): Call<User>

    @POST("api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("api/users/{userId}")
    fun getUser(@Path("userId") userId: Int, @Header("Authorization") token: String): Call<User>

    // Device endpoints
    @GET("api/users/{userId}/devices")
    fun getUserDevices(@Path("userId") userId: Int, @Header("Authorization") token: String): Call<List<Device>>

    @POST("api/devices")
    fun registerDevice(@Body device: Device, @Header("Authorization") token: String): Call<Device>

    @PUT("api/devices/{deviceId}")
    fun updateDevice(
        @Path("deviceId") deviceId: Int,
        @Body device: Device,
        @Header("Authorization") token: String
    ): Call<Device>

    // Health data endpoints
    @POST("api/health-data")
    fun sendHealthData(
        @Body healthData: List<HealthData>,
        @Header("Authorization") token: String
    ): Call<List<HealthData>>

    @GET("api/devices/{deviceId}/health-data")
    fun getDeviceHealthData(
        @Path("deviceId") deviceId: Int,
        @Query("dataType") dataType: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Header("Authorization") token: String
    ): Call<List<HealthData>>

    @GET("api/users/{userId}/health-data")
    fun getUserHealthData(
        @Path("userId") userId: Int,
        @Query("dataType") dataType: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Header("Authorization") token: String
    ): Call<List<HealthData>>
}

class HealthApiClient(context: Context) {
    private val TAG = "HealthApiClient"
    private val BASE_URL = "http://your-api-base-url/" // Change to your server URL
    private val apiService: HealthApiService
    private val sharedPreferences = context.getSharedPreferences("health_app_prefs", Context.MODE_PRIVATE)
    private var authToken: String? = null
    private var currentUser: User? = null

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(HealthApiService::class.java)

        // Load saved token if available
        authToken = sharedPreferences.getString("auth_token", null)
        val userId = sharedPreferences.getInt("user_id", -1)
        if (userId != -1) {
            fetchUserProfile(userId)
        }
    }

    // User Authentication Methods

    fun register(
        username: String,
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = User(
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            password = password
        )

        apiService.registerUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        onSuccess(user)
                    } ?: onError("Empty response")
                } else {
                    onError("Registration failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }

    fun login(
        username: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        val loginRequest = LoginRequest(username, password)

        apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        authToken = "Bearer ${loginResponse.token}"
                        currentUser = loginResponse.user

                        // Save to shared preferences
                        sharedPreferences.edit().apply {
                            putString("auth_token", authToken)
                            putInt("user_id", currentUser?.id ?: -1)
                        }.apply()

                        onSuccess(loginResponse.user)
                    } ?: onError("Empty response")
                } else {
                    onError("Login failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }

    fun logout() {
        authToken = null
        currentUser = null

        // Clear shared preferences
        sharedPreferences.edit().apply {
            remove("auth_token")
            remove("user_id")
        }.apply()
    }

    private fun fetchUserProfile(userId: Int) {
        authToken?.let { token ->
            apiService.getUser(userId, token).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        currentUser = response.body()
                    } else {
                        // Token might be expired, clear it
                        logout()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e(TAG, "Failed to fetch user profile", t)
                }
            })
        }
    }

    // Device Methods

    fun getUserDevices(
        onSuccess: (List<Device>) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = currentUser?.id ?: run {
            onError("User not logged in")
            return
        }

        val token = authToken ?: run {
            onError("Not authenticated")
            return
        }

        apiService.getUserDevices(userId, token).enqueue(object : Callback<List<Device>> {
            override fun onResponse(call: Call<List<Device>>, response: Response<List<Device>>) {
                if (response.isSuccessful) {
                    response.body()?.let { devices ->
                        onSuccess(devices)
                    } ?: onError("Empty response")
                } else {
                    onError("Failed to get devices: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Device>>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }

    fun registerDevice(
        deviceName: String,
        deviceType: DeviceType,
        onSuccess: (Device) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = currentUser?.id ?: run {
            onError("User not logged in")
            return
        }

        val token = authToken ?: run {
            onError("Not authenticated")
            return
        }

        val device = Device(
            deviceName = deviceName,
            deviceType = deviceType,
            user = User(id = userId, username = "", email = "", firstName = "", lastName = "")
        )

        apiService.registerDevice(device, token).enqueue(object : Callback<Device> {
            override fun onResponse(call: Call<Device>, response: Response<Device>) {
                if (response.isSuccessful) {
                    response.body()?.let { device ->
                        onSuccess(device)
                    } ?: onError("Empty response")
                } else {
                    onError("Failed to register device: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Device>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }

    // Health Data Methods

    fun syncGoogleFitData(
        googleFitHelper: GoogleFitHelper,
        deviceId: Int,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = authToken ?: run {
            onError("Not authenticated")
            return
        }

        // Get detailed step data
        googleFitHelper.fetchDetailedStepData(
            numberOfDays = 7,
            onSuccess = { dailyStepDataList ->
                val healthDataList = mutableListOf<HealthData>()

                // Convert GoogleFit step data to our HealthData model
                dailyStepDataList.forEach { dailyData ->
                    // Add the daily total as one data point
                    healthDataList.add(
                        HealthData(
                            deviceId = deviceId,
                            measuredAt = "${dailyData.date}T00:00:00",
                            dataType = DataTypes.STEPS,
                            valueNumeric = dailyData.totalSteps,
                            unit = "steps"
                        )
                    )

                    // Add hourly data points for more detail
                    dailyData.hourlySteps.forEach { hourlyData ->
                        val formattedHour = hourlyData.hour.toString().padStart(2, '0')
                        healthDataList.add(
                            HealthData(
                                deviceId = deviceId,
                                measuredAt = "${dailyData.date}T${formattedHour}:00:00",
                                dataType = DataTypes.STEPS,
                                valueNumeric = hourlyData.steps,
                                unit = "steps"
                            )
                        )
                    }
                }

                // Also fetch heart rate data
                googleFitHelper.fetchAllFitnessData(
                    onSuccess = { fitnessData ->
                        // Add heart rate data if available
                        if (fitnessData.heartRate > 0) {
                            healthDataList.add(
                                HealthData(
                                    deviceId = deviceId,
                                    measuredAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                    dataType = DataTypes.HEARTBEAT,
                                    valueNumeric = fitnessData.heartRate.toInt(),
                                    unit = "bpm"
                                )
                            )
                        }

                        // Send all health data to server
                        sendHealthData(healthDataList, onSuccess, onError)
                    },
                    onFailure = { e ->
                        // Even if we can't get fitness data, try to send step data
                        if (healthDataList.isNotEmpty()) {
                            sendHealthData(healthDataList, onSuccess, onError)
                        } else {
                            onError("Failed to fetch fitness data: ${e.message}")
                        }
                    }
                )
            },
            onFailure = { e ->
                onError("Failed to fetch step data: ${e.message}")
            }
        )
    }

    private fun sendHealthData(
        healthDataList: List<HealthData>,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val token = authToken ?: run {
            onError("Not authenticated")
            return
        }

        if (healthDataList.isEmpty()) {
            onError("No health data to send")
            return
        }

        apiService.sendHealthData(healthDataList, token).enqueue(object : Callback<List<HealthData>> {
            override fun onResponse(call: Call<List<HealthData>>, response: Response<List<HealthData>>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        onSuccess(data.size)
                    } ?: onError("Empty response")
                } else {
                    onError("Failed to send health data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<HealthData>>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }

    fun getUserHealthData(
        dataType: DataTypes? = null,
        startDate: String? = null,
        endDate: String? = null,
        onSuccess: (List<HealthData>) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = currentUser?.id ?: run {
            onError("User not logged in")
            return
        }

        val token = authToken ?: run {
            onError("Not authenticated")
            return
        }

        apiService.getUserHealthData(
            userId = userId,
            dataType = dataType?.toString(),
            startDate = startDate,
            endDate = endDate,
            token = token
        ).enqueue(object : Callback<List<HealthData>> {
            override fun onResponse(call: Call<List<HealthData>>, response: Response<List<HealthData>>) {
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        onSuccess(data)
                    } ?: onError("Empty response")
                } else {
                    onError("Failed to get health data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<HealthData>>, t: Throwable) {
                onError("Network error: ${t.message}")
            }
        })
    }
}