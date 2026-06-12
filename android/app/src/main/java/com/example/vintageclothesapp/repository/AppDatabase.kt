package com.example.vintageclothesapp.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.vintageclothesapp.model.ClothingItemEntity

@Database(
    entities = [ClothingItemEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clothingItemDao(): ClothingItemDao
}