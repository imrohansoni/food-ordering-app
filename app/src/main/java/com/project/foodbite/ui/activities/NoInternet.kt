package com.project.foodbite.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.foodbite.databinding.ActivityNoInternetBinding
import com.project.foodbite.utils.AuthTokenManager
import com.project.foodbite.utils.NetworkConnectivity

class NoInternet : AppCompatActivity() {
    private lateinit var networkConnectivity: NetworkConnectivity
    private lateinit var binding: ActivityNoInternetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoInternetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkConnectivity.registerNetworkCallback {
            if (it) {
                val authToken = AuthTokenManager.getAuthToken(this)
                if (authToken != null) {
                    Intent(this, MainActivity::class.java).apply {
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkConnectivity.unregisterNetworkCallback()
    }
}