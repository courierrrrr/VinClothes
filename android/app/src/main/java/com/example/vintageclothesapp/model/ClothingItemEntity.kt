package com.example.vintageclothesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItemEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val brand: String,
    val size: String,
    val decade: String,
    val clothing_type: String,
    val description: String,
    val video_url: String,
    val average_rating: Double,
    val image: String?
)