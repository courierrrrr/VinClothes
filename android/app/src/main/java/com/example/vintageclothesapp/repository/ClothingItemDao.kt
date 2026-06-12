package com.example.vintageclothesapp.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.vintageclothesapp.model.ClothingItemEntity

@Dao
interface ClothingItemDao {

    @Query("SELECT * FROM clothing_items")
    suspend fun getAll(): List<ClothingItemEntity>

    @Query("SELECT * FROM clothing_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ClothingItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ClothingItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ClothingItemEntity>)
}