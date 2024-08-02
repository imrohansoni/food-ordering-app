package com.project.foodorderingapp.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.foodorderingapp.R
import com.project.foodorderingapp.adapters.OrderAdapter
import com.project.foodorderingapp.database.FoodbiteDatabase
import com.project.foodorderingapp.databinding.ActivityOrderBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)

        lifecycleScope.launch(Dispatchers.IO) {
            val orders = FoodbiteDatabase.getDatabase(applicationContext).orderDao().getAllOrders()
            withContext(Dispatchers.Main) {

                binding.orderList.layoutManager = LinearLayoutManager(applicationContext)
                binding.orderList
                    .adapter = OrderAdapter(orders)

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}