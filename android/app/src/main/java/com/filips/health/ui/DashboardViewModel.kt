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
        loadCurrentUser()
        loadHealthData()
    }

    private fun loadCurrentUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = User(
                            id = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            username = document.getString("username") ?: ""
                        )
                        _currentUser.value = user
                    }
                }
        }
    }

    private fun loadHealthData() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("health_data")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val healthData = HealthData(
                        steps = document.getLong("steps")?.toInt() ?: 0,
                        distance = document.getDouble("distance") ?: 0.0,
                        calories = document.getDouble("calories") ?: 0.0,
                        heartRate = document.getLong("heartRate")?.toInt() ?: 0
                    )
                    _healthData.value = healthData
                    _lastSync.value = document.getDate("lastSync") ?: Date()
                }
            }
    }

    fun syncHealthData() {
        viewModelScope.launch {
            // TODO: Implement Google Fit sync
            // For now, just update last sync time
            _lastSync.value = Date()
        }
    }

    fun logout() {
        auth.signOut()
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