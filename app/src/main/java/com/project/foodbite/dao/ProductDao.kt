package com.project.foodorderingapp.dao

import androidx.room.Dao
import androidx.room.Query
import com.project.foodorderingapp.models.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    fun getAllProducts(categoryId: Int): List<Product>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :s || '%' OR description LIKE '%' || :s || '%'")
    fun searchProducts(s: String): List<Product>

}