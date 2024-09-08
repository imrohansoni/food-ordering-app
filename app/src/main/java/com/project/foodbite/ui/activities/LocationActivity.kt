package com.project.foodbite.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.foodbite.controllers.LocationController
import com.project.foodbite.databinding.ActivityLocationBinding
import com.project.foodbite.utils.Constants
import kotlinx.coroutines.launch

class LocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var controller: LocationController

    private val gpsSettingLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            lifecycleScope.launch {
                controller.getCurrentLocation()
            }
        } else {
            Toast.makeText(
                this,
                "GPS is required for this feature. Please enable it.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLocationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        controller = LocationController(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.locationAnimationView.setAnimation("location_animation.json")
        binding.locationAnimationView.playAnimation()

        binding.enableDeviceLocationButton.setOnClickListener {
            checkLocationSettings()
        }

        binding.enterLocationManuallyButton.setOnClickListener {
            Intent(this@LocationActivity, AddressActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private fun checkLocationSettings() {
        if (!controller.isLocationPermissionAllowed()) {
            if (controller.shouldRequestPermissionAgain()) {
                controller.requestLocationPermission()
            } else {
                showLocationPermissionDialog()
            }
        } else {
            if (!controller.isGpsEnabled()) {
                lifecycleScope.launch {
                    controller.displayGpsSettingDialog(gpsSettingLauncher)
                }
            } else {
                lifecycleScope.launch {
                    controller.getCurrentLocation()
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkLocationSettings()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationSettings()
        } else {
            showLocationPermissionDialog()
        }
    }

    private fun showLocationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This app needs location access to provide its services. Please grant the permission.")
            .setPositiveButton("Open settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(
                "Cancel"
            ) { _, _ ->
                Toast.makeText(
                    this@LocationActivity,
                    "Please grant the location permission to continue",
                    Toast.LENGTH_SHORT
                ).show()
                binding.enableDeviceLocationButton.stopLoading()
            }
            .show()
    }
}