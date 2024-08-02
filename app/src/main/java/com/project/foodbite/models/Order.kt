package com.project.foodorderingapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders",
//    foreignKeys = [
//        ForeignKey(
//            entity = Product::class,
//            parentColumns = ["id"],
//            childColumns = ["productId"]
//        )
//    ]
)

data class Order(
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
