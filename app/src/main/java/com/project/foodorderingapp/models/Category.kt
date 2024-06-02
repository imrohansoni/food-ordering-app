package com.project.foodorderingapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val id: Int,
    val categoryName: String,
    val imageUrl: String
)