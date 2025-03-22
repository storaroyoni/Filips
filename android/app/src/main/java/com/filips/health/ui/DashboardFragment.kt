package com.filips.health.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.googlefitapp.GoogleFitHelper
import com.filips.health.MainActivity
import com.filips.health.R
import com.filips.health.databinding.FragmentDashboardBinding
import com.filips.health.databinding.DialogEditProfileBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var currentName = "John Doe"
    private var currentEmail = "john.doe@example.com"
    private lateinit var googleFitHelper: GoogleFitHelper
    private val RC_SIGN_IN = MainActivity.RC_SIGN_IN

    companion object {
        private const val TAG = "DashboardFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleFitHelper = GoogleFitHelper(requireContext())
        
        setupUserProfile()
        setupEmptyCharts()
        setupClickListeners()
        
        // Initialize with empty data
        clearSummaryData()
        
        // Check if already connected to Google Fit
        checkGoogleFitConnection()
    }

    private fun checkGoogleFitConnection() {
        Log.d(TAG, "Checking Google Fit connection. Signed in: ${googleFitHelper.isSignedIn()}, Has permissions: ${googleFitHelper.hasPermissions()}")
        
        if (googleFitHelper.isSignedIn() && googleFitHelper.hasPermissions()) {
            // Hide "no data" message and fetch data
            binding.noDataMessage.visibility = View.GONE
            fetchFitnessData()
        } else {
            // Show message to connect to Google Fit
            binding.noDataMessage.visibility = View.VISIBLE
        }
    }

    private fun setupUserProfile() {
        binding.nameTextView.text = currentName
        binding.emailTextView.text = currentEmail
    }

    private fun clearSummaryData() {
        binding.stepsCount.text = "0"
        binding.distanceValue.text = "0.0"
        binding.caloriesValue.text = "0"
        binding.heartRateValue.text = "0"
    }
    
    private fun setupEmptyCharts() {
        setupStepsChart(emptyList())
        setupHeartRateChart(emptyList())
        setupSleepChart(emptyList())
    }

    private fun setupStepsChart(stepData: List<GoogleFitHelper.HourlyStepData>) {
        val chart = binding.stepsChart
        
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(getHourLabels())
        
        chart.axisLeft.setDrawGridLines(true)
        chart.axisRight.isEnabled = false

        val entries = ArrayList<Entry>()
        
        if (stepData.isNotEmpty()) {
            // Use real data
            for (hourData in stepData) {
                entries.add(Entry(hourData.hour.toFloat(), hourData.steps.toFloat()))
            }
        }

        val dataSet = LineDataSet(entries, "Steps")
        dataSet.color = Color.parseColor("#4CAF84")
        dataSet.setCircleColor(Color.parseColor("#4CAF84"))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()
    }

    private fun setupHeartRateChart(heartRateData: List<Pair<Int, Int>>) {
        val chart = binding.heartRateChart
        
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(getHourLabels())
        
        chart.axisLeft.setDrawGridLines(true)
        chart.axisRight.isEnabled = false

        val entries = ArrayList<Entry>()
        
        if (heartRateData.isNotEmpty()) {
            for ((hour, rate) in heartRateData) {
                entries.add(Entry(hour.toFloat(), rate.toFloat()))
            }
        }

        val dataSet = LineDataSet(entries, "Heart Rate")
        dataSet.color = Color.parseColor("#4CAF84")
        dataSet.setCircleColor(Color.parseColor("#4CAF84"))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()
    }

    private fun setupSleepChart(sleepData: List<Pair<String, Float>>) {
        val chart = binding.sleepChart
        
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        
        val stageLabels = if (sleepData.isNotEmpty()) {
            sleepData.map { it.first }.toTypedArray()
        } else {
            getSleepStageLabels()
        }
        
        xAxis.valueFormatter = IndexAxisValueFormatter(stageLabels)
        
        chart.axisLeft.setDrawGridLines(true)
        chart.axisRight.isEnabled = false

        val entries = ArrayList<BarEntry>()
        
        if (sleepData.isNotEmpty()) {
            for (i in sleepData.indices) {
                entries.add(BarEntry(i.toFloat(), sleepData[i].second))
            }
        }

        val dataSet = BarDataSet(entries, "Sleep Stages")
        dataSet.color = Color.parseColor("#4CAF84")
        dataSet.setDrawValues(false)

        val barData = BarData(dataSet)
        chart.data = barData
        chart.invalidate()

        // Only update sleep duration and quality if we have real data
        if (sleepData.isNotEmpty()) {
            val totalMinutes = sleepData.sumOf { it.second.toInt() }
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            binding.sleepDurationValue.text = "${hours}h ${minutes}m"
            
            // Calculate a simple sleep quality score based on ratio of deep sleep and REM
            val deepIndex = sleepData.indexOfFirst { it.first == "Deep" }
            val remIndex = sleepData.indexOfFirst { it.first == "REM" }
            
            val deepSleep = if (deepIndex >= 0) sleepData[deepIndex].second else 0f
            val remSleep = if (remIndex >= 0) sleepData[remIndex].second else 0f
            
            if (totalMinutes > 0) {
                val qualityScore = ((deepSleep + remSleep) / totalMinutes * 100).toInt()
                binding.sleepQualityValue.text = "$qualityScore%"
            } else {
                binding.sleepQualityValue.text = "N/A"
            }
        } else {
            binding.sleepDurationValue.text = "0h 0m"
            binding.sleepQualityValue.text = "N/A"
        }
    }

    private fun setupClickListeners() {
        binding.editProfileButton.setOnClickListener {
            showEditProfileDialog()
        }

        binding.syncButton.setOnClickListener {
            if (googleFitHelper.isSignedIn() && googleFitHelper.hasPermissions()) {
                fetchFitnessData()
            } else {
                connectToGoogleFit()
            }
        }
        
        binding.signOutButton.setOnClickListener {
            if (googleFitHelper.isSignedIn()) {
                googleFitHelper.signOut {
                    // Update UI after sign-out
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                        binding.noDataMessage.visibility = View.VISIBLE
                        clearSummaryData()
                        setupEmptyCharts()
                    }
                }
            } else {
                Toast.makeText(context, "Not signed in", Toast.LENGTH_SHORT).show()
            }
        }

        binding.viewForumButton.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_forum)
        }
    }

    private fun connectToGoogleFit() {
        Log.d(TAG, "Connecting to Google Fit")
        
        if (!googleFitHelper.isSignedIn()) {
            // Not signed in, start the sign-in flow
            Log.d(TAG, "Not signed in, starting Google sign-in")
            startActivityForResult(googleFitHelper.getSignInIntent(), RC_SIGN_IN)
            return
        }
        
        // Already signed in but may need permissions
        if (!googleFitHelper.hasPermissions()) {
            Log.d(TAG, "Requesting Google Fit permissions")
            // Request the Fitness permissions
            GoogleSignIn.requestPermissions(
                this,
                GoogleFitHelper.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(requireContext()),
                googleFitHelper.fitnessOptions
            )
            return
        }
        
        // Already signed in and has permissions
        Log.d(TAG, "Already signed in with permissions, fetching data")
        fetchFitnessData()
    }

    private fun fetchFitnessData() {
        Log.d(TAG, "Fetching fitness data")
        binding.syncProgress.visibility = View.VISIBLE
        binding.syncButton.isEnabled = false
        binding.noDataMessage.visibility = View.GONE
        
        googleFitHelper.fetchStepData(
            numberOfDays = 7,
            onSuccess = { dailyStepDataList ->
                Log.d(TAG, "Successfully fetched step data: ${dailyStepDataList.size} days")
                activity?.runOnUiThread {
                    binding.syncProgress.visibility = View.GONE
                    binding.syncButton.isEnabled = true
                    binding.lastSyncText.text = "Last sync: ${getCurrentTime()}"
                    binding.lastSyncText.visibility = View.VISIBLE
                    
                    if (dailyStepDataList.isNotEmpty()) {
                        // Update the step count with the most recent day's data
                        val todayData = dailyStepDataList.firstOrNull()
                        todayData?.let {
                            Log.d(TAG, "Today's data: ${it.date}, steps: ${it.totalSteps}, hourly entries: ${it.hourlySteps.size}")
                            binding.stepsCount.text = it.totalSteps.toString()
                            setupStepsChart(it.hourlySteps)
                            
                            // Calculate approximate distance (rough estimate: 0.7m per step)
                            val distanceKm = (it.totalSteps * 0.7 / 1000)
                            binding.distanceValue.text = String.format("%.1f", distanceKm)
                            
                            // Calculate approximate calories (rough estimate: 0.04 calories per step)
                            val calories = (it.totalSteps * 0.04).toInt()
                            binding.caloriesValue.text = calories.toString()
                        }
                    } else {
                        Log.d(TAG, "No step data found")
                        Toast.makeText(context, "No step data found", Toast.LENGTH_SHORT).show()
                        clearSummaryData()
                        setupEmptyCharts()
                    }
                }
            },
            onFailure = { e ->
                Log.e(TAG, "Failed to fetch step data", e)
                activity?.runOnUiThread {
                    binding.syncProgress.visibility = View.GONE
                    binding.syncButton.isEnabled = true
                    Toast.makeText(context, "Failed to fetch data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")

        when (requestCode) {
            RC_SIGN_IN -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Sign-in successful")
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    googleFitHelper.handleSignInResult(
                        task,
                        onSuccess = {
                            Log.d(TAG, "Sign-in task successful, checking permissions")
                            // Now that we're signed in, check for fitness permissions
                            if (!googleFitHelper.hasPermissions()) {
                                Log.d(TAG, "Requesting fitness permissions")
                                GoogleSignIn.requestPermissions(
                                    this,
                                    GoogleFitHelper.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                                    GoogleSignIn.getLastSignedInAccount(requireContext()),
                                    googleFitHelper.fitnessOptions
                                )
                            } else {
                                Log.d(TAG, "Already has permissions, fetching data")
                                fetchFitnessData()
                            }
                        },
                        onFailure = { e ->
                            Log.e(TAG, "Sign-in task failed", e)
                            Toast.makeText(context, "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Log.e(TAG, "Sign-in failed with resultCode: $resultCode")
                    Toast.makeText(context, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
                }
            }
            GoogleFitHelper.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Fitness permissions granted, fetching data")
                    fetchFitnessData()
                } else {
                    Log.e(TAG, "Fitness permissions denied with resultCode: $resultCode")
                    Toast.makeText(context, "Google Fit permissions denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if connection state changed while fragment was not visible
        checkGoogleFitConnection()
    }

    private fun showEditProfileDialog() {
        val dialogBinding = DialogEditProfileBinding.inflate(layoutInflater)
        
        dialogBinding.nameEditText.setText(currentName)
        dialogBinding.emailEditText.setText(currentEmail)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.saveButton.setOnClickListener {
            val newName = dialogBinding.nameEditText.text.toString()
            val newEmail = dialogBinding.emailEditText.text.toString()

            if (newName.isBlank() || newEmail.isBlank()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentName = newName
            currentEmail = newEmail
            setupUserProfile()
            
            dialog.dismiss()
            Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun getHourLabels(): Array<String> {
        return Array(24) { "${it}:00" }
    }

    private fun getSleepStageLabels(): Array<String> {
        return arrayOf("Deep", "Light", "REM", "Awake")
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}