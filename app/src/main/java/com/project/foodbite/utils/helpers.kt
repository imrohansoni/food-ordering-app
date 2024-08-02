package com.project.foodbite.utils

import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log

fun checkPermission(context: Context, permission: String): Boolean {
    return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

fun isGpsEnabled(context : Context): Boolean {
    val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}


fun logger(tag: String = "FOODBITE_LOGGER", message: String = "this is the logger") {
    Log.d(tag, message)
}