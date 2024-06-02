package com.project.foodorderingapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "carts",
)
data class Cart(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    var quantity: Int,
    val name: String,
    val price: Double,
    val discount: String,
    val image: String,
    val rating: Double,
)