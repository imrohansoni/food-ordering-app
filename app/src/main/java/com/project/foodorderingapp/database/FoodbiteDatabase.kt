package com.project.foodorderingapp.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.foodorderingapp.dao.CartDao
import com.project.foodorderingapp.dao.CategoryDao
import com.project.foodorderingapp.dao.FavoriteDao
import com.project.foodorderingapp.dao.OrderDao
import com.project.foodorderingapp.dao.ProductDao
import com.project.foodorderingapp.dao.UserDao
import com.project.foodorderingapp.models.Cart
import com.project.foodorderingapp.models.Category
import com.project.foodorderingapp.models.Favorite
import com.project.foodorderingapp.models.Order
import com.project.foodorderingapp.models.Product
import com.project.foodorderingapp.models.User

@Database(
    entities = [User::class, Cart::class, Order::class, Favorite::class, Product::class, Category::class],
    version = 1
)
abstract class FoodbiteDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun categoryDao(): CategoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun productDao(): ProductDao
    abstract fun userDao() : UserDao
    abstract fun cartDao() : CartDao

    companion object {
        @Volatile
        private var Instance: FoodbiteDatabase? = null
        fun getDatabase(context: Context): FoodbiteDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = FoodbiteDatabase::class.java,
                    name = "foodbite_db"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}