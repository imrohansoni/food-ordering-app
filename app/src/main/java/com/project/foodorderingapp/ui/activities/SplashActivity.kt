package com.project.foodorderingapp.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.project.foodorderingapp.R
import com.project.foodorderingapp.utils.AuthTokenManager
import com.project.foodorderingapp.utils.LocationHelper
import com.project.foodorderingapp.utils.NetworkConnectivity

class SplashActivity : AppCompatActivity() {
    private lateinit var networkConnectivity: NetworkConnectivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = Color.BLACK
        checkInternetConnectivity()
    }

    private fun checkInternetConnectivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            val network = NetworkConnectivity(this).isNetworkAvailable()

            if (!network) {
                Intent(this, NoInternet::class.java).apply {
                    startActivity(this)
                    finish()
                }
            } else {
                val authToken = AuthTokenManager.getAuthToken(this)
                val locationHelper = LocationHelper(applicationContext)
//                Intent(this@SplashActivity, MainActivity::class.java ).apply {
//                    startActivity(this)
//                    finish()
//                }
                if (authToken != null) {
                    if (locationHelper.isGpsEnabled() && locationHelper.checkLocationPermission()) {
                        Intent(this, MainActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                        return@postDelayed
                    }
                    Intent(this, LocationActivity::class.java).apply {
                        startActivity(this)
                        finish()
                    }
                } else {
                    Intent(this, LoginActivity::class.java).apply {
                        startActivity(this)
                        finish()
                    }
                }
            }
        }, 1000)
    }
}