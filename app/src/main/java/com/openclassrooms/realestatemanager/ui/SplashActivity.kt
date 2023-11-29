package com.openclassrooms.realestatemanager.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimation()
        makePermissionRequest()
    }

    private fun setupAnimation() {
        val progressText = binding.permissionTextView

        progressText.text = getString(R.string.check_permission_1)

        lifecycleScope.launch {
            delay(1000)
            progressText.text = getString(R.string.check_permission_2)
            delay(1000)
            progressText.text = getString(R.string.check_permission_3)
            delay(1000)
            progressText.text = getString(R.string.check_permission_4)
        }
    }

    private fun makePermissionRequest() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private val locationPermissionRequest =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION]
            val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION]

            if (fine == true && coarse == true) {
                // Location permission granted.
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                }, 3000)
                startActivity(intent)
            } else {
                // Location permission rejected.
                showAlertDialog()
            }
        }

    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(getString(R.string.splash_dialog_message))
        alertDialogBuilder.setPositiveButton(
            getString(R.string.positive_string)
        ) { _, _ ->
            try {
                // Access app settings screen
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID, null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@SplashActivity,
                    getString(R.string.splash_dialog_settings_error) + " " + e,
                    Toast.LENGTH_LONG
                ).show()
                Log.d("error", e.toString())
            }
        }
        alertDialogBuilder.setNegativeButton(getString(R.string.negative_string)) { _, _ ->
            Toast.makeText(
                this@SplashActivity,
                getString(R.string.splash_dialog_message_warning),
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
