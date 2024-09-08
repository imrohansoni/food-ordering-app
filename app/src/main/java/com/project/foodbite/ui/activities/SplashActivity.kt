package com.project.foodbite.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.project.foodbite.databinding.ActivitySplashBinding
import com.project.foodbite.utils.NetworkConnectivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                Intent(this, LoginActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
//                val authToken = AuthTokenManager.getAuthToken(this)
//
//                if (authToken != null) {
//                    Intent(this, MainActivity::class.java).apply {
//                        startActivity(this)
//                        finish()
//                    }
//                } else {
//                    Intent(this, LoginActivity::class.java).apply {
//                        startActivity(this)
//                        finish()
//                    }
//                }
            }
        }, 1000)
    }
}