package com.project.foodorderingapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "products",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"]
    )]
)

data class Product(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val discount: String,
    val type: String,
    val categoryId: Int,
    val image: String,
    val rating: Double,
    val deliveryTime: Int
) : Serializable
