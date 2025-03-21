package com.example.googlefitapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var googleFitHelper: GoogleFitHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleFitHelper = GoogleFitHelper(this)
        setContent {
            MaterialTheme {
                val showRawJson = remember { mutableStateOf(false) }
                
                if (showRawJson.value) {
                    JsonDataApp(googleFitHelper) {
                        showRawJson.value = false
                    }
                } else {
                    StepDataApp(googleFitHelper) {
                        showRawJson.value = true
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_SIGN_IN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                googleFitHelper.handleSignInResult(
                    task,
                    onSuccess = {
                        if (!googleFitHelper.hasPermissions()) {
                            GoogleSignIn.requestPermissions(
                                this,
                                GoogleFitHelper.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                                GoogleSignIn.getLastSignedInAccount(this),
                                googleFitHelper.fitnessOptions
                            )
                        }
                    },
                    onFailure = { e -> Log.e("GoogleFitApp", "Sign-in failed", e) }
                )
            }
            GoogleFitHelper.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    Log.d("GoogleFitApp", "Google Fit permissions granted")
                    // The UI will update via the LaunchedEffect
                } else {
                    Log.e("GoogleFitApp", "Google Fit permissions denied")
                }
            }
        }
    }
}

@Composable
fun StepDataApp(googleFitHelper: GoogleFitHelper, onShowJsonView: () -> Unit) {
    val context = LocalContext.current
    var stepDataList by remember { mutableStateOf<List<GoogleFitHelper.DailyStepData>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var numberOfDays by remember { mutableStateOf(7) }
    val coroutineScope = rememberCoroutineScope()

    // Check if already signed in and has permissions
    LaunchedEffect(googleFitHelper.isSignedIn(), googleFitHelper.hasPermissions()) {
        if (googleFitHelper.isSignedIn() && googleFitHelper.hasPermissions()) {
            isLoading = true
            fetchDetailedStepData(
                googleFitHelper,
                numberOfDays,
                onSuccess = { 
                    stepDataList = it
                    isLoading = false
                },
                onFailure = { 
                    errorMessage = it
                    isLoading = false
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Step Count by Date and Hour",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Number of days selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Last",
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            listOf(3, 7, 14, 30).forEach { days ->
                OutlinedButton(
                    onClick = {
                        numberOfDays = days
                        if (googleFitHelper.isSignedIn() && googleFitHelper.hasPermissions()) {
                            isLoading = true
                            fetchDetailedStepData(
                                googleFitHelper,
                                numberOfDays,
                                onSuccess = { 
                                    stepDataList = it
                                    isLoading = false
                                },
                                onFailure = { 
                                    errorMessage = it
                                    isLoading = false
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (numberOfDays == days) MaterialTheme.colorScheme.primary else Color.Gray
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(text = "$days days")
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .size(50.dp)
            )
            Text(text = "Loading step data...")
        } else if (stepDataList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(stepDataList) { dailyData ->
                    DailyStepCard(dailyData)
                }
            }
        } else if (!googleFitHelper.isSignedIn() || !googleFitHelper.hasPermissions()) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Please sign in with Google and grant access to your fitness data to view your step count history.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "No step data found for the selected period.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    errorMessage = null
                    coroutineScope.launch {
                        val activity = context as ComponentActivity
                        if (!googleFitHelper.isSignedIn()) {
                            // Sign in with Google
                            activity.startActivityForResult(googleFitHelper.getSignInIntent(), RC_SIGN_IN)
                        } else if (!googleFitHelper.hasPermissions()) {
                            // Show permission dialog
                            showPermissionDialog = true
                        } else {
                            isLoading = true
                            fetchDetailedStepData(
                                googleFitHelper,
                                numberOfDays,
                                onSuccess = { 
                                    stepDataList = it
                                    isLoading = false
                                },
                                onFailure = { 
                                    errorMessage = it
                                    isLoading = false
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when {
                        !googleFitHelper.isSignedIn() -> "Sign In with Google"
                        !googleFitHelper.hasPermissions() -> "Request Permissions"
                        else -> "Refresh Data"
                    }
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            OutlinedButton(
                onClick = onShowJsonView,
                enabled = googleFitHelper.isSignedIn() && googleFitHelper.hasPermissions(),
                modifier = Modifier.width(120.dp)
            ) {
                Text("Show JSON")
            }
        }
    }
    
    // Permission Dialog
    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onAllow = {
                showPermissionDialog = false
                val activity = context as ComponentActivity
                GoogleSignIn.requestPermissions(
                    activity,
                    GoogleFitHelper.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    googleFitHelper.fitnessOptions
                )
            },
            onDeny = {
                showPermissionDialog = false
                errorMessage = "Google Fit permissions are required to access your step data"
            }
        )
    }
}

@Composable
fun JsonDataApp(googleFitHelper: GoogleFitHelper, onBackToStepView: () -> Unit) {
    val context = LocalContext.current
    var jsonData by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var numberOfDays by remember { mutableStateOf(7) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Check if already signed in and has permissions
    LaunchedEffect(googleFitHelper.isSignedIn(), googleFitHelper.hasPermissions()) {
        if (googleFitHelper.isSignedIn() && googleFitHelper.hasPermissions()) {
            isLoading = true
            fetchSleepAndTodayStepsAsJson(
                googleFitHelper,
                numberOfDays,
                onSuccess = { 
                    jsonData = it
                    isLoading = false
                },
                onFailure = { 
                    errorMessage = it
                    isLoading = false
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sleep Data and Today's Steps (JSON)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Number of days selector for sleep data
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sleep data for last",
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            listOf(3, 7, 14, 30).forEach { days ->
                OutlinedButton(
                    onClick = {
                        numberOfDays = days
                        if (googleFitHelper.isSignedIn() && googleFitHelper.hasPermissions()) {
                            isLoading = true
                            fetchSleepAndTodayStepsAsJson(
                                googleFitHelper,
                                numberOfDays,
                                onSuccess = { 
                                    jsonData = it
                                    isLoading = false
                                },
                                onFailure = { 
                                    errorMessage = it
                                    isLoading = false
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (numberOfDays == days) MaterialTheme.colorScheme.primary else Color.Gray
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(text = "$days days")
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .size(50.dp)
            )
            Text(text = "Loading data...")
        } else if (jsonData.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = jsonData,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }
        } else if (!googleFitHelper.isSignedIn() || !googleFitHelper.hasPermissions()) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Please sign in with Google and grant access to your fitness data to view your sleep and step data.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "No data found.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    errorMessage = null
                    coroutineScope.launch {
                        val activity = context as ComponentActivity
                        if (!googleFitHelper.isSignedIn()) {
                            // Sign in with Google
                            activity.startActivityForResult(googleFitHelper.getSignInIntent(), RC_SIGN_IN)
                        } else if (!googleFitHelper.hasPermissions()) {
                            // Show permission dialog
                            showPermissionDialog = true
                        } else {
                            isLoading = true
                            fetchSleepAndTodayStepsAsJson(
                                googleFitHelper,
                                numberOfDays,
                                onSuccess = { 
                                    jsonData = it
                                    isLoading = false
                                },
                                onFailure = { 
                                    errorMessage = it
                                    isLoading = false
                                }
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = when {
                        !googleFitHelper.isSignedIn() -> "Sign In with Google"
                        !googleFitHelper.hasPermissions() -> "Request Permissions"
                        else -> "Refresh JSON"
                    }
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            OutlinedButton(
                onClick = onBackToStepView,
                modifier = Modifier.width(120.dp)
            ) {
                Text("Back to Steps")
            }
        }
    }
    
    // Permission Dialog
    if (showPermissionDialog) {
        PermissionDialog(
            onDismiss = { showPermissionDialog = false },
            onAllow = {
                showPermissionDialog = false
                val activity = context as ComponentActivity
                GoogleSignIn.requestPermissions(
                    activity,
                    GoogleFitHelper.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(activity),
                    googleFitHelper.fitnessOptions
                )
            },
            onDeny = {
                showPermissionDialog = false
                errorMessage = "Google Fit permissions are required to access your fitness data"
            }
        )
    }
}

@Composable
fun DailyStepCard(dailyData: GoogleFitHelper.DailyStepData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dailyData.date,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Text(
                    text = "Total: ${dailyData.totalSteps} steps",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            if (dailyData.hourlySteps.isEmpty()) {
                Text(
                    text = "No hourly data available",
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = "Hourly Breakdown:",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                dailyData.hourlySteps.forEach { hourlyData ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Format hour with AM/PM
                        val hourFormatted = when {
                            hourlyData.hour == 0 -> "12 AM"
                            hourlyData.hour < 12 -> "${hourlyData.hour} AM"
                            hourlyData.hour == 12 -> "12 PM"
                            else -> "${hourlyData.hour - 12} PM"
                        }
                        
                        Text(text = hourFormatted)
                        Text(text = "${hourlyData.steps} steps")
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onAllow: () -> Unit,
    onDeny: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Google Fit Permissions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This app requires access to your Google Fit data to display your step count and sleep information.",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onDeny,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Deny")
                    }
                    Button(
                        onClick = onAllow
                    ) {
                        Text("Allow")
                    }
                }
            }
        }
    }
}

private fun fetchDetailedStepData(
    googleFitHelper: GoogleFitHelper,
    numberOfDays: Int,
    onSuccess: (List<GoogleFitHelper.DailyStepData>) -> Unit,
    onFailure: (String) -> Unit
) {
    Log.d("GoogleFitApp", "Fetching detailed step data for last $numberOfDays days")
    googleFitHelper.fetchDetailedStepData(
        numberOfDays = numberOfDays,
        onSuccess = { dailyStepDataList ->
            Log.d("GoogleFitApp", "Received step data: $dailyStepDataList")
            onSuccess(dailyStepDataList)
        },
        onFailure = { e ->
            Log.e("GoogleFitApp", "Failed to fetch step data", e)
            onFailure("Failed to fetch step data: ${e.message}")
        }
    )
}

private fun fetchSleepAndTodayStepsAsJson(
    googleFitHelper: GoogleFitHelper,
    numberOfDays: Int,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    Log.d("GoogleFitApp", "Fetching sleep and today's steps as JSON for last $numberOfDays days")
    googleFitHelper.fetchSleepAndTodayStepsAsJson(
        numberOfDays = numberOfDays,
        onSuccess = { jsonData ->
            Log.d("GoogleFitApp", "Received JSON data")
            onSuccess(jsonData)
        },
        onFailure = { e ->
            Log.e("GoogleFitApp", "Failed to fetch JSON data", e)
            onFailure("Failed to fetch data: ${e.message}")
        }
    )
}

private const val RC_SIGN_IN = 9001