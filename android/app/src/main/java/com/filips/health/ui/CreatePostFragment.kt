package com.filips.health.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.filips.health.R
import com.filips.health.model.ForumPost
import com.filips.health.model.HealthStats
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class CreatePostFragment : Fragment() {
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var tvSteps: TextView
    private lateinit var tvHeartRate: TextView
    private lateinit var tvSleep: TextView
    private lateinit var btnPost: MaterialButton
    private lateinit var switchAnonymous: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupToolbar(view)
        loadHealthData()
        setupPostButton()
    }

    private fun initViews(view: View) {
        etTitle = view.findViewById(R.id.etTitle)
        etDescription = view.findViewById(R.id.etDescription)
        tvSteps = view.findViewById(R.id.tvSteps)
        tvHeartRate = view.findViewById(R.id.tvHeartRate)
        tvSleep = view.findViewById(R.id.tvSleep)
        btnPost = view.findViewById(R.id.btnPost)
        switchAnonymous = view.findViewById(R.id.switchAnonymous)
    }

    private fun setupToolbar(view: View) {
        view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun loadHealthData() {
        // TODO: Load real health data from Google Fit
        tvSteps.text = "8,543"
        tvHeartRate.text = "72 bpm"
        tvSleep.text = "7.5 hrs"
    }

    private fun setupPostButton() {
        btnPost.setOnClickListener {
            val title = etTitle.text?.toString()
            val description = etDescription.text?.toString()

            if (title.isNullOrBlank() || description.isNullOrBlank()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val post = ForumPost(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                isAnonymous = switchAnonymous.isChecked,
                authorName = if (switchAnonymous.isChecked) "Anonymous" else "John Doe", // Replace with actual user name
                timestamp = System.currentTimeMillis(),
                healthStats = HealthStats(
                    steps = tvSteps.text.toString(),
                    heartRate = tvHeartRate.text.toString(),
                    sleepHours = tvSleep.text.toString()
                )
            )

            findNavController().previousBackStackEntry?.savedStateHandle?.set("new_post", post)
            
            Toast.makeText(
                context,
                "Posted ${if (switchAnonymous.isChecked) "anonymously" else "publicly"}",
                Toast.LENGTH_SHORT
            ).show()
            
            findNavController().navigateUp()
        }
    }
} 