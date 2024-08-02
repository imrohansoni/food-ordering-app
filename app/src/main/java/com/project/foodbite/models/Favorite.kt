package com.project.foodorderingapp.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites", foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"]
        )
    ]
)

data class Favorite(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val productId: Int,
)