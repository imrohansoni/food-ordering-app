package com.project.foodorderingapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.foodorderingapp.R
import com.project.foodorderingapp.databinding.ActivityOrderBinding
import com.project.foodorderingapp.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyPolicyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}