package com.filips.health.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.filips.health.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserInfo()
        setupHealthStats()
        setupClickListeners()
    }

    private fun setupUserInfo() {
        // TODO: Replace with actual user data
        binding.nameTextView.text = "John Doe"
        binding.emailTextView.text = "john.doe@example.com"
    }

    private fun setupHealthStats() {
        // TODO: Replace with actual health data
        binding.stepsTextView.text = "12,500"
        binding.heartRateTextView.text = "72"
        binding.caloriesTextView.text = "450"
        binding.distanceTextView.text = "8.5"
    }

    private fun setupClickListeners() {
        binding.editProfileButton.setOnClickListener {
            // TODO: Implement edit profile functionality
            Toast.makeText(context, "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }

        binding.signOutButton.setOnClickListener {
            // TODO: Implement sign out functionality
            Toast.makeText(context, "Sign Out clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 