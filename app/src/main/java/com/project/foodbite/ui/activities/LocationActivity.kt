package com.project.foodbite.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.project.foodbite.databinding.ActivityLocationBinding
import com.project.foodbite.utils.Constants
import com.project.foodbite.utils.checkPermission
import com.project.foodbite.utils.isGpsEnabled

class LocationActivity : AppCompatActivity() {
    private lateinit var fusedLocationProvider: FusedLocationProviderClient
    private lateinit var binding: ActivityLocationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLocationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        binding.locationAccessButton.setOnClickListener {
            binding.locationAccessButton.startLoading("getting location...")
            checkOrRequestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (isGpsEnabled(applicationContext)) {
            fusedLocationProvider.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { currentLocation ->
                currentLocation?.let {
                    Toast.makeText(
                        this@LocationActivity,
                        "got the location ${it.latitude} and ${it.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Failed to get current location: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun checkOrRequestPermission() {
        if (checkPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) && checkPermission(
                applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            getCurrentLocation()
        } else {
            requestPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        checkOrRequestPermission()
    }

}