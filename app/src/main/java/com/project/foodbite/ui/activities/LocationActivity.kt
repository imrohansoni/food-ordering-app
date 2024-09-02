package com.project.foodbite.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.project.foodbite.databinding.ActivityLocationBinding
import com.project.foodbite.utils.Constants
import com.project.foodbite.utils.checkPermission
import com.project.foodbite.utils.isGpsEnabled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LocationActivity : AppCompatActivity() {
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var binding: ActivityLocationBinding

    private val gpsSettingLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            lifecycleScope.launch {
                getCurrentLocation()
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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.locationAnimationView.setAnimation("location_animation.json")
        binding.locationAnimationView.playAnimation()

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        binding.enableDeviceLocationButton.setOnClickListener {
            checkForLocation()
        }

        binding.enterLocationManuallyButton.setOnClickListener {
            Intent(this@LocationActivity, AddressActivity::class.java).apply {
                startActivity(this)
            }
        }
    }

    private suspend fun showTurnOnGpsDialog() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setMaxUpdates(1)
            .build()

        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        val settingsClient = LocationServices.getSettingsClient(this)

        try {
            settingsClient.checkLocationSettings(request).await()
            getCurrentLocation()
        } catch (exception: ResolvableApiException) {
            val intentSenderRequest =
                IntentSenderRequest.Builder(exception.resolution).build()
            gpsSettingLauncher.launch(intentSenderRequest)
        } catch (exception: Exception) {
            binding.enableDeviceLocationButton.stopLoading()

            Toast.makeText(
                this@LocationActivity,
                "GPS is required for this app to work. Please enable GPS.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun shouldRequestPermission(): Boolean {
        return (ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun checkForLocation() {
        if (!checkLocationPermission()) {
            if (shouldRequestPermission()) {
                requestLocationPermission()
            } else {
                showLocationPermissionDialog()
            }
        } else {
            if (!isGpsEnabled(this)) {
                lifecycleScope.launch {
                    showTurnOnGpsDialog()
                }
            } else {
                lifecycleScope.launch {
                    getCurrentLocation()
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        checkForLocation()
    }

    private fun checkLocationPermission(): Boolean {
        return checkPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) &&
                checkPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this@LocationActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation() {
        try {
            binding.enableDeviceLocationButton.startLoading("getting the location")
            val location = withContext(Dispatchers.IO) {
                fusedLocationProvider.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()
            }

            location?.let {
                withContext(Dispatchers.Main) {
                    binding.enableDeviceLocationButton.stopLoading()
                    Intent(this@LocationActivity, MainActivity::class.java).apply {
                        startActivity(this)
                        finishAffinity()
                    }
                    // save the location to the database
                }
            }
        } catch (e: Exception) {
            binding.enableDeviceLocationButton.stopLoading()
            var message = "Failed to get current location"
            if (e is SecurityException) {
                message = "Location permission was denied."
            }

            Toast.makeText(
                this@LocationActivity, message,
                Toast.LENGTH_SHORT
            ).show()
        }
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
            checkForLocation()
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



