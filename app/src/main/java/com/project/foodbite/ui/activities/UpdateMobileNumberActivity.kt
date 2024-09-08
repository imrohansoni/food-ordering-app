package com.project.foodbite.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.project.foodbite.R
import com.project.foodbite.databinding.ActivityUpdateMobileNumberBinding

class UpdateMobileNumberActivity : AppCompatActivity() {
    private lateinit var b: ActivityUpdateMobileNumberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityUpdateMobileNumberBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)
    }

}