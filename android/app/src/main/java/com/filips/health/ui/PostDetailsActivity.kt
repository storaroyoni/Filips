package com.filips.health.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.filips.health.databinding.ActivityPostDetailsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class PostDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        displayPostDetails()
        setupChart()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = intent.getStringExtra("TITLE") ?: "Post Details"
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun displayPostDetails() {
        binding.apply {
            authorTextView.text = intent.getStringExtra("AUTHOR") ?: "Anonymous"
            contentTextView.text = intent.getStringExtra("DESCRIPTION") ?: ""
            
            stepsTextView.text = intent.getStringExtra("STEPS") ?: "0 steps"
            heartRateTextView.text = intent.getStringExtra("HEART_RATE") ?: "0 bpm"
            caloriesTextView.text = "0 cal"
            distanceTextView.text = "0 km"
        }
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(arrayOf("Mon", "Tue", "Wed", "Thu", "Fri"))
                labelRotationAngle = 0f
            }
            
            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 50f
                axisMaximum = 100f
            }
            axisRight.isEnabled = false
            
            val entries = listOf(
                com.github.mikephil.charting.data.Entry(0f, 70f),
                com.github.mikephil.charting.data.Entry(1f, 72f),
                com.github.mikephil.charting.data.Entry(2f, 75f),
                com.github.mikephil.charting.data.Entry(3f, 73f),
                com.github.mikephil.charting.data.Entry(4f, 74f)
            )
            
            val dataSet = LineDataSet(entries, "Heart Rate").apply {
                color = getColor(com.filips.health.R.color.primary)
                setCircleColor(getColor(com.filips.health.R.color.primary))
                lineWidth = 2f
                circleRadius = 4f
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
            
            data = LineData(dataSet)
            invalidate()
        }
    }
} 