package com.project.foodorderingapp.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.project.foodorderingapp.network.apiServices
import com.project.foodorderingapp.databinding.ActivityLocationBinding
import com.project.foodorderingapp.utils.Constants
import com.project.foodorderingapp.utils.LocationHelper
import kotlinx.coroutines.launch

class LocationActivity : AppCompatActivity() {
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var binding: ActivityLocationBinding
    private lateinit var locationHelper: LocationHelper

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            binding = ActivityLocationBinding.inflate(layoutInflater)

            result.locations.lastOrNull()?.let {
                onLocationResult(it.latitude, it.longitude)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLocationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        locationHelper = LocationHelper(applicationContext)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        binding.locationAccessButton.setOnClickListener {
            binding.locationAccessButton.startLoading("getting location...")

            if (locationHelper.checkLocationPermission()) {
                getCurrentLocation()
            } else {
                requestPermission()
            }
        }
    }

    private fun onLocationResult(latitude: Double, longitude: Double) {
        val data = hashMapOf(
            "lat" to latitude,
            "long" to longitude
        )

        lifecycleScope.launch {
            try {
                val res =
                    apiServices(context = applicationContext, tokenRequired = true)
                        .updateLocation(data)
                if (res.isSuccessful && res.body() != null) {
                    Intent(this@LocationActivity, MainActivity::class.java).apply {
                        startActivity(this)
                        finish()
                    }
                }
            } catch (th: Throwable) {
                Toast.makeText(this@LocationActivity, "something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            if (locationHelper.checkLocationPermission()) {
                getCurrentLocation()
            } else {
                binding.locationAccessButton.stopLoading()
                Toast.makeText(
                    this,
                    "please allow the location permission to continue",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (locationHelper.isGpsEnabled()) {
            val locationRequest = LocationRequest
                .Builder(5000L)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build()

            fusedLocationProvider.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                startActivity(this)
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onResume() {
        super.onResume()
        val locationHelper = LocationHelper(applicationContext)
        if (locationHelper.checkLocationPermission() && locationHelper.isGpsEnabled()) {
            getCurrentLocation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProvider.removeLocationUpdates(locationCallback)
    }
}