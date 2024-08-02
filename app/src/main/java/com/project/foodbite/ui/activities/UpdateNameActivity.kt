package com.project.foodorderingapp.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.foodorderingapp.database.FoodbiteDatabase
import com.project.foodorderingapp.R
import com.project.foodorderingapp.network.apiServices
import com.project.foodorderingapp.databinding.ActivityUpdateNameBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateNameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateNameBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateNameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)


        lifecycleScope.launch(Dispatchers.IO) {
            val db = FoodbiteDatabase.getDatabase(this@UpdateNameActivity).userDao()
            val user = db.getUser()[0]

            if (user.firstName != null && user.lastName != null) {
                withContext(Dispatchers.Main) {
                    binding.firstNameEditText.setText(user.firstName)
                    binding.lastNameEditText.setText(user.lastName)
                }
            }
        }

        binding.updateNameButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString()
            val lastName = binding.lastNameEditText.text.toString()
            val data = hashMapOf(
                "first_name" to firstName,
                "last_name" to lastName
            )
//            lifecycleScope.launch {
//                val res = apiServices(this@UpdateNameActivity, true).updateName(data)
//
//                if (res.isSuccessful && res.body() != null) {
//                    Toast.makeText(
//                        this@UpdateNameActivity,
//                        res.body()!!.data,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}