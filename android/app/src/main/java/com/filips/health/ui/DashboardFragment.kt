package com.filips.health.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.filips.health.R
import com.filips.health.data.model.HealthData
import com.filips.health.databinding.FragmentDashboardBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels { DashboardViewModel.Factory }
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

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
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        with(binding) {
            syncButton.setOnClickListener {
                viewModel.syncHealthData()
            }

            viewForumButton.setOnClickListener {
                findNavController().navigate(R.id.action_dashboard_to_forum)
            }

            logoutButton.setOnClickListener {
                viewModel.logout()
                findNavController().navigate(R.id.action_dashboard_to_login)
            }

            editProfileButton.setOnClickListener {
                findNavController().navigate(R.id.action_dashboard_to_profile)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.userNameText.text = user.username
                binding.userEmailText.text = user.email
                binding.welcomeText.text = getString(R.string.welcome_user, user.username)
            }
        }

        viewModel.healthData.observe(viewLifecycleOwner) { healthData ->
            updateHealthDataUI(healthData)
        }

        viewModel.lastSync.observe(viewLifecycleOwner) { lastSync ->
            binding.lastSyncText.text = getString(R.string.last_sync, dateFormat.format(lastSync))
        }
    }

    private fun updateHealthDataUI(healthData: HealthData?) {
        with(binding) {
            if (healthData == null) {
                stepsCount.text = "0"
                distanceValue.text = "0.0"
                caloriesValue.text = "0"
                heartRateValue.text = "--"
                return
            }

            stepsCount.text = healthData.steps.toString()
            distanceValue.text = String.format("%.1f", healthData.distance / 1000) // Convert to km
            caloriesValue.text = healthData.calories.toInt().toString()
            heartRateValue.text = if (healthData.heartRate > 0)
                healthData.heartRate.toString() else "--"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}