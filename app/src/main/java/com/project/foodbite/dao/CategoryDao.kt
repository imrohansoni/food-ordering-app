package com.project.foodorderingapp.dao

import androidx.room.Dao
import androidx.room.Query
import com.project.foodorderingapp.models.Category

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<Category>
}