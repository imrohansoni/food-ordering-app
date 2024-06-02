package com.project.foodorderingapp.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.airbnb.lottie.LottieAnimationView
import com.project.foodorderingapp.R

class ConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        val animation = findViewById<LottieAnimationView>(R.id.confirmation_animation)

        animation.setAnimation("confirmation.json")
        window.statusBarColor = Color.parseColor("#F04F5F")

        // Start the animation
        animation.playAnimation()

        // Optionally, set additional properties
        animation.loop(true)
        showNotification()
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, "foodbite")
            .setSmallIcon(R.drawable.logo_icon)
            .setContentTitle("Order confirmed")
            .setContentText("Congratulations, your order is confirmed 😊😊")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(1, builder.build())
        }
    }
}