package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.local.dao.BookingDao
import com.example.myapplication.data.local.dao.ImageDao
import com.example.myapplication.data.local.dao.RoomDao
import com.example.myapplication.data.local.dao.UserDao
import com.example.myapplication.data.local.entity.BookingEntity
import com.example.myapplication.data.local.entity.ImageEntity
import com.example.myapplication.data.local.entity.RoomEntity
import com.example.myapplication.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, ImageEntity::class, RoomEntity::class, BookingEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun imageDao(): ImageDao
    abstract fun roomDao(): RoomDao
    abstract fun bookingDao(): BookingDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
