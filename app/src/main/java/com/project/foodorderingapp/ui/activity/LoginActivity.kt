package com.project.foodorderingapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.project.foodorderingapp.R

class LoginActivity : AppCompatActivity() {
    private lateinit var mobileNumberEditText: EditText
    private lateinit var continueButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mobileNumberEditText = findViewById(R.id.mobile_number_edit_text)
        continueButton = findViewById(R.id.continue_button)

        continueButton.setOnClickListener {
            val mobileNumber = mobileNumberEditText.text
            if (mobileNumber.length != 10) {
                Toast.makeText(this, "Please enter a valid mobile number", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, VerifyCodeActivity::class.java)
                intent.putExtra("mobileNumber", mobileNumber.toString())
                startActivity(intent)
            }

        }
    }
}