package com.project.foodorderingapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.foodorderingapp.R
import com.project.foodorderingapp.utils.AuthTokenManager
import com.project.foodorderingapp.utils.NetworkConnectivity

class NoInternet : AppCompatActivity() {
    private lateinit var networkConnectivity: NetworkConnectivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
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