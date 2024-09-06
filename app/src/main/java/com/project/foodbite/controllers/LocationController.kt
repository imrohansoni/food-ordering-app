package com.project.foodbite.controllers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.project.foodbite.utils.Constants
import com.project.foodbite.utils.checkPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LocationController(private val activity: Activity) {
    private lateinit var fusedLocationProvider: FusedLocationProviderClient

    fun isLocationPermissionAllowed(): Boolean {
        return checkPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) &&
                checkPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    fun shouldRequestPermissionAgain(): Boolean {
        return (ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            activity, Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            Constants.LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    fun isGpsEnabled(): Boolean {
        val locationManager =
            activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    suspend fun displayGpsSettingDialog(gpsSettingLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setMaxUpdates(1)
            .build()

        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        val settingsClient = LocationServices.getSettingsClient(activity)

        try {
            settingsClient.checkLocationSettings(request).await()
        } catch (exception: Exception) {
            if (exception is ResolvableApiException) {
                val intentSenderRequest =
                    IntentSenderRequest.Builder(exception.resolution).build()
                gpsSettingLauncher.launch(intentSenderRequest)
            } else {
                throw exception
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location {
        try {
            val location = withContext(Dispatchers.IO) {
                fusedLocationProvider.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()
            }

            return location
        } catch (e: Exception) {
            throw e
        }
    }
}



