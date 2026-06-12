package com.example.vintageclothesapp.repository

import com.example.vintageclothesapp.ClothingItem

interface ClothingRepository {
    suspend fun getClothes(type: String?, decade: String?, size: String?, brand: String?): List<ClothingItem>
    suspend fun getClothingById(id: Int): ClothingItem
    suspend fun addClothing(item: ClothingItem)
}