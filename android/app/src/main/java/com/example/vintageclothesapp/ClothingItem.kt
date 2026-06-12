package com.example.vintageclothesapp

data class ClothingItem(
    val id: Int = 0,
    val title: String,
    val decade: String,
    val brand: String,
    val size: String,
    val type: String,
    val description: String,
    val imageUrl: String = "",
    val videoUrl: String = "",
    val imageBase64: String? = null,
    val averageRating: Double = 0.0
)