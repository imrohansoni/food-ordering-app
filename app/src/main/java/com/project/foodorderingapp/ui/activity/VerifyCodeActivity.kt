package com.project.foodorderingapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.project.foodorderingapp.R

class VerifyCodeActivity : AppCompatActivity() {
    private lateinit var verificationCodeTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_code)
        val mobileNumber = intent.extras?.getString("mobileNumber")

        verificationCodeTextView = findViewById(R.id.verification_screen_text_view)
        verificationCodeTextView.text = "${verificationCodeTextView.text} ${mobileNumber}"
    }
}