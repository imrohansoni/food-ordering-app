package com.project.foodbite.ui.activities


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.project.foodbite.R
import com.project.foodbite.databinding.ActivityMainBinding
import com.project.foodbite.ui.fragments.Account
import com.project.foodbite.ui.fragments.Home
import com.project.foodbite.ui.fragments.Restaurant


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()

        navigateToFragment(Home())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    navigateToFragment(Home())
                    true
                }

                R.id.restaurant -> {
                    navigateToFragment(Restaurant())
                    true
                }

                R.id.account -> {
                    navigateToFragment(Account())
                    true
                }

                else -> {
                    navigateToFragment(Home())
                    true
                }
            }
        }
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("foodbite", "foodbite", importance)
        channel.description = "foodbite notification"
        // Register the channel with the system
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .addToBackStack(null)
            .commit()
    }
}
