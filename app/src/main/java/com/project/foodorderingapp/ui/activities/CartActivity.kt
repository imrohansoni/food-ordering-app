package com.project.foodorderingapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.foodorderingapp.R
import com.project.foodorderingapp.adapters.CartItemAdapter
import com.project.foodorderingapp.database.FoodbiteDatabase
import com.project.foodorderingapp.databinding.ActivityCartBinding
import com.project.foodorderingapp.models.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartItemAdapter: CartItemAdapter
    private var isActive: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_button)

        binding.confirmation.setOnClickListener {
            if (isActive) {
                Intent(this, ConfirmationActivity::class.java).apply {
                    startActivity(this)
                    lifecycleScope.launch(Dispatchers.IO) {
                        FoodbiteDatabase.getDatabase(applicationContext).cartDao().getAllCartItems()
                            .forEach {
                                FoodbiteDatabase.getDatabase(applicationContext).orderDao()
                                    .createOrder(
                                        Order(
                                            name = it.name,
                                            price = it.price,
                                            discount = it.discount,
                                            rating = it.rating,
                                            quantity = it.quantity,
                                            image = it.image,
                                            productId = it.productId
                                        )
                                    )
                            }

                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val cartDao = FoodbiteDatabase.getDatabase(this@CartActivity).cartDao()
            val carts = cartDao.getAllCartItems().toMutableList()
            var sum = 0.0
            carts.forEach {
                sum += it.quantity * it.price
            }
            setTotalPrice(sum)
            cartItemAdapter = CartItemAdapter(
                carts,
                addToCart = { cart, sum ->
                    setTotalPrice(sum)
                    lifecycleScope.launch(Dispatchers.IO) {
                        cartDao.increaseQuantity(cart.id)
                    }
                },
                removeFromCart = { cart, sum ->
                    setTotalPrice(sum)
                    lifecycleScope.launch(Dispatchers.IO) {
                        if (cart.quantity == 0) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@CartActivity,
                                    "${cart.name} is removed from the cart",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            cartDao.deleteFromCart(cart)
                        }
                        cartDao.decreaseQuantity(cart.id)
                    }
                }
            )

            binding.cartList.apply {
                layoutManager = LinearLayoutManager(this@CartActivity)
                adapter = cartItemAdapter
            }
        }
    }

    private fun setTotalPrice(sum: Double) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.totalAmount.text = "₹ ${sum}"
        }

        if (sum >= 0) {
            isActive = true
        } else {
            isActive = false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
