package com.project.foodorderingapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities

class NetworkConnectivity(private val context: Context) {
    private lateinit var networkCallback: NetworkCallback
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return (capabilities != null) &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    fun registerNetworkCallback(callback: (Boolean) -> Unit) {
        networkCallback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                callback(true)
            }

            override fun onLost(network: Network) {
                callback(false)
            }

            override fun onUnavailable() {
                callback(false)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}