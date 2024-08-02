package com.project.foodorderingapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.foodorderingapp.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUserData(user: User)

    @Query("SELECT * FROM users")
    fun getUser(): List<User>
}