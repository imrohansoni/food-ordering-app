package com.project.foodorderingapp.ui.activities


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.project.foodorderingapp.R
import com.project.foodorderingapp.database.FoodbiteDatabase
import com.project.foodorderingapp.databinding.ActivityMainBinding
import com.project.foodorderingapp.models.User
import com.project.foodorderingapp.network.apiServices
import com.project.foodorderingapp.ui.fragments.Account
import com.project.foodorderingapp.ui.fragments.Home
import com.project.foodorderingapp.ui.fragments.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// name
// email
// mobile number
// privacy policy
// terms and conditions
// about

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val res = apiServices(this@MainActivity, tokenRequired = true).getAccount()
                if (res.isSuccessful && res.body() != null) {
                    withContext(Dispatchers.IO) {
                        FoodbiteDatabase.getDatabase(this@MainActivity).userDao()
                            .saveUserData(
                                user = User(
                                    id = 1,
                                    firstName = res.body()!!.data.firstName,
                                    lastName = res.body()!!.data.lastName,
                                    email = res.body()!!.data.email,
                                    mobileNumber = res.body()!!.data.mobileNumber
                                )
                            )
                    }

                }
            } catch (throwable: Throwable) {
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, "something went wrong", Toast.LENGTH_SHORT).show()
                }
            }

        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("foodbite", "foodbite", importance)
            channel.description = "foodbite notification"
            // Register the channel with the system
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .addToBackStack(null)
            .commit()
    }
}
