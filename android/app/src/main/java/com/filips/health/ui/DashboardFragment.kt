package com.filips.health.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.filips.health.R
import com.filips.health.databinding.FragmentDashboardBinding
import com.filips.health.databinding.DialogEditProfileBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private var currentName = "John Doe"
    private var currentEmail = "john.doe@example.com"

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
        setupUserProfile()
        setupSummaryData()
        setupStepsChart()
        setupHeartRateChart()
        setupSleepChart()
        setupClickListeners()
    }

    private fun setupUserProfile() {
        binding.nameTextView.text = currentName
        binding.emailTextView.text = currentEmail
    }

    private fun setupSummaryData() {
        // Set sample data for summary card
        binding.stepsCount.text = "8,743"
        binding.distanceValue.text = "6.2"
        binding.caloriesValue.text = "420"
        binding.heartRateValue.text = "72"
    }

    private fun setupStepsChart() {
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
        val hourlySteps = generateSampleStepsData()
        for (i in hourlySteps.indices) {
            entries.add(Entry(i.toFloat(), hourlySteps[i].toFloat()))
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

    private fun setupHeartRateChart() {
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
        val hourlyHeartRate = generateSampleHeartRateData()
        for (i in hourlyHeartRate.indices) {
            entries.add(Entry(i.toFloat(), hourlyHeartRate[i].toFloat()))
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

    private fun setupSleepChart() {
        val chart = binding.sleepChart
        
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(getSleepStageLabels())
        
        chart.axisLeft.setDrawGridLines(true)
        chart.axisRight.isEnabled = false

        val entries = ArrayList<BarEntry>()
        val sleepStages = generateSampleSleepData()
        for (i in sleepStages.indices) {
            entries.add(BarEntry(i.toFloat(), sleepStages[i].toFloat()))
        }

        val dataSet = BarDataSet(entries, "Sleep Stages")
        dataSet.color = Color.parseColor("#4CAF84")
        dataSet.setDrawValues(false)

        val barData = BarData(dataSet)
        chart.data = barData
        chart.invalidate()

        binding.sleepDurationValue.text = "7h 30m"
        binding.sleepQualityValue.text = "85%"
    }

    private fun setupClickListeners() {
        binding.editProfileButton.setOnClickListener {
            showEditProfileDialog()
        }

        binding.syncButton.setOnClickListener {
            binding.lastSyncText.text = "Last sync: ${getCurrentTime()}"
        }

        binding.viewForumButton.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_forum)
        }
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

    private fun generateSampleStepsData(): List<Int> {
        return List(24) { (100..1000).random() }
    }

    private fun generateSampleHeartRateData(): List<Int> {
        return List(24) { (60..100).random() }
    }

    private fun generateSampleSleepData(): List<Int> {
        return listOf(120, 240, 90, 30) // Minutes in each sleep stage
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