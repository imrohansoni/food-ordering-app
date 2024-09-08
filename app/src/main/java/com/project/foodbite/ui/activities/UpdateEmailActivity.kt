package com.project.foodbite.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.foodbite.R
import com.project.foodbite.database.FoodbiteDatabase
import com.project.foodbite.databinding.ActivityUpdateEmailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdateEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)
//        lifecycleScope.launch(Dispatchers.IO) {
//            val db = FoodbiteDatabase.getDatabase(this@UpdateEmailActivity).userDao()
//            val user = db.getUser()[0]
//
//            if (user.email != null) {
//                withContext(Dispatchers.Main) {
//                    binding.emailEditText.setText(user.email)
//                }
//            }
//        }

        binding.updateEmailButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val data = hashMapOf(
                "email" to email,
            )
//            lifecycleScope.launch {
//                val res = apiServices(this@UpdateEmailActivity, true).updateEmail(data)
//
//                if (res.isSuccessful && res.body() != null) {
//                    Toast.makeText(
//                        this@UpdateEmailActivity,
//                        res.body()!!.data,
//                        Toast.LENGTH_SHORT
//                    ).show()
//
//                    withContext(Dispatchers.IO) {
//                        val db = FoodbiteDatabase.getDatabase(this@UpdateEmailActivity).userDao()
//                        val user = db.getUser()[0]
//
//                        db.saveUserData(
//                            user = User(
//                                id = 1,
//                                mobileNumber = user.mobileNumber,
//                                email = email,
//                                firstName = user.firstName,
//                                lastName = user.lastName
//                            )
//                        )
//                    }
//                }
//            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}