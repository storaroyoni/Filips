package com.filips.health.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.filips.health.data.model.HealthData
import com.filips.health.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.random.Random

class DashboardViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _healthData = MutableLiveData<HealthData?>()
    val healthData: LiveData<HealthData?> = _healthData

    private val _lastSync = MutableLiveData<Date>()
    val lastSync: LiveData<Date> = _lastSync

    init {
        loadDummyData()
    }

    private fun loadDummyData() {
        // Dummy user data
        _currentUser.value = User(
            id = "dummy_id",
            email = "john.doe@example.com",
            username = "John Doe"
        )

        _healthData.value = HealthData(
            steps = 8743,
            distance = 6.2 * 1000, // 6.2 km in meters
            calories = 420.0,
            heartRate = 72
        )

        _lastSync.value = Date()
    }

    fun syncHealthData() {
        viewModelScope.launch {
            // Generate new random but realistic data when syncing
            _healthData.value = HealthData(
                steps = Random.nextInt(6000, 12000),
                distance = Random.nextDouble(4.0, 8.0) * 1000, // 4-8 km in meters
                calories = Random.nextDouble(300.0, 600.0),
                heartRate = Random.nextInt(65, 85)
            )
            _lastSync.value = Date()
        }
    }

    fun logout() {
        _currentUser.value = null
        _healthData.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel() as T
            }
        }
    }
} 