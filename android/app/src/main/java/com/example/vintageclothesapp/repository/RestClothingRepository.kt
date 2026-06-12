// repository/RestClothingRepository.kt

package com.example.vintageclothesapp.repository

import com.example.vintageclothesapp.ClothingApiService
import com.example.vintageclothesapp.ClothingItem
import com.example.vintageclothesapp.ClothingItemDto
import com.example.vintageclothesapp.model.ClothingItemEntity

class RestClothingRepository(
    private val api: ClothingApiService,
    private val dao: ClothingItemDao
) : ClothingRepository {

    override suspend fun getClothes(type: String?, decade: String?, size: String?, brand: String?): List<ClothingItem> {
        return try {
            val remote = api.getClothes(type, decade, size, brand)
            dao.insertAll(remote.map { dtoToEntity(it) })
            remote.map { dtoToClothingItem(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getClothingById(id: Int): ClothingItem {
        return try {
            val remote = api.getClothingById(id)
            dao.insert(dtoToEntity(remote))
            dtoToClothingItem(remote)
        } catch (e: Exception) {
            val cached = dao.getById(id)
            cached?.let { entityToClothingItem(it) }
                ?: throw Exception("Item not found")
        }
    }

    override suspend fun addClothing(item: ClothingItem) {
        val dto = ClothingItemDto(
            title = item.title,
            brand = item.brand,
            size = item.size,
            decade = item.decade,
            clothing_type = item.type,
            description = item.description,
            video_url = item.videoUrl,
            image = item.imageBase64
        )
        val saved = api.addClothing(dto)
        dao.insert(dtoToEntity(saved))
    }

    private fun dtoToEntity(dto: ClothingItemDto): ClothingItemEntity {
        return ClothingItemEntity(
            id = dto.id ?: 0,
            title = dto.title,
            brand = dto.brand,
            size = dto.size,
            decade = dto.decade,
            clothing_type = dto.clothing_type,
            description = dto.description,
            video_url = dto.video_url,
            average_rating = dto.average_rating,
            image = dto.image
        )
    }

    private fun dtoToClothingItem(dto: ClothingItemDto): ClothingItem {
        return ClothingItem(
            id = dto.id ?: 0,
            title = dto.title,
            brand = dto.brand,
            size = dto.size,
            decade = dto.decade,
            type = dto.clothing_type,
            description = dto.description,
            videoUrl = dto.video_url,
            imageBase64 = dto.image,
            averageRating = dto.average_rating
        )
    }

    private fun entityToClothingItem(entity: ClothingItemEntity): ClothingItem {
        return ClothingItem(
            id = entity.id,
            title = entity.title,
            brand = entity.brand,
            size = entity.size,
            decade = entity.decade,
            type = entity.clothing_type,
            description = entity.description,
            videoUrl = entity.video_url,
            imageBase64 = entity.image,
            averageRating = entity.average_rating
        )
    }
}