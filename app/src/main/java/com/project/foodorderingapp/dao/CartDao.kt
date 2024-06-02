package com.project.foodorderingapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.project.foodorderingapp.models.Cart

@Dao
interface CartDao {
    @Query("SELECT * FROM carts")
    fun getAllCartItems(): List<Cart>

    @Upsert
    fun updateCart(cart: Cart)

    @Delete
    fun deleteFromCart(cart: Cart)

    @Query("UPDATE carts SET quantity = quantity + 1 WHERE id = :cartId")
    fun increaseQuantity(cartId: Int)

    @Query("UPDATE carts SET quantity = quantity - 1 WHERE id = :cartId AND quantity > 1")
    fun decreaseQuantity(cartId: Int)

    @Query("SELECT SUM(price) FROM carts")
    fun getTotalPrice() : Double
}