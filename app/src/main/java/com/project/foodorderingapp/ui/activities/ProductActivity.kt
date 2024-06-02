package com.project.foodorderingapp.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.foodorderingapp.R
import com.project.foodorderingapp.database.FoodbiteDatabase
import com.project.foodorderingapp.adapters.ProductAdapter
import com.project.foodorderingapp.databinding.ActivityProductBinding
import com.project.foodorderingapp.models.Cart
import com.project.foodorderingapp.models.Products
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProductBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)

        val products = intent.getSerializableExtra("PRODUCT_LIST") as Products
        binding.productsList.layoutManager = LinearLayoutManager(this)
        binding.productsList.adapter = ProductAdapter(products) {
            lifecycleScope.launch(Dispatchers.IO) {
                FoodbiteDatabase.getDatabase(this@ProductActivity).cartDao()
                    .updateCart(
                        cart = Cart(
                            productId = it.id,
                            quantity = 1,
                            name = it.name,
                            price = it.price,
                            discount = it.discount,
                            image = it.image,
                            rating = it.rating
                        )
                    )
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ProductActivity,
                        "${it.name} is added to the cart",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}