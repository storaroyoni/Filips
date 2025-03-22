package com.filips.health

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.googlefitapp.GoogleFitHelper
import com.filips.health.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var googleFitHelper: GoogleFitHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Google Fit Helper
        googleFitHelper = GoogleFitHelper(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "Activity result: requestCode=$requestCode, resultCode=$resultCode")

        // Pass the activity result to the current fragment first
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()
        currentFragment?.onActivityResult(requestCode, resultCode, data)

        // Also handle results at activity level (as backup)
        when (requestCode) {
            RC_SIGN_IN -> {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Sign-in successful at activity level")
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    googleFitHelper.handleSignInResult(
                        task,
                        onSuccess = {
                            if (!googleFitHelper.hasPermissions()) {
                                Log.d(TAG, "Requesting fitness permissions from activity")
                                GoogleSignIn.requestPermissions(
                                    this,
                                    GoogleFitHelper.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                                    GoogleSignIn.getLastSignedInAccount(this),
                                    googleFitHelper.fitnessOptions
                                )
                            }
                        },
                        onFailure = { e -> Log.e(TAG, "Sign-in failed at activity level", e) }
                    )
                } else {
                    Log.e(TAG, "Sign-in failed or was cancelled by user with result code: $resultCode")
                }
            }
            GoogleFitHelper.GOOGLE_FIT_PERMISSIONS_REQUEST_CODE -> {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Google Fit permissions granted at activity level")
                } else {
                    Log.e(TAG, "Google Fit permissions denied at activity level")
                }
            }
        }
    }

    companion object {
        const val RC_SIGN_IN = 9001
        private const val TAG = "MainActivity"
    }
}