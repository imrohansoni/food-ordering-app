package com.project.foodorderingapp.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.project.foodorderingapp.models.Favorite
import com.project.foodorderingapp.models.Product

@Dao
interface FavoriteDao {
    @Query("SELECT products.* FROM favorites JOIN products ON products.id = favorites.productId")
    fun getAllFavorites(): List<Product>

    @Upsert
    fun addToFavorite(favorite: Favorite)

    @Delete
    fun removeFromFavorite(favorite: Favorite)
}