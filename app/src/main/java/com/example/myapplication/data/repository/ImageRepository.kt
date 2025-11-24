package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.ImageDao
import com.example.myapplication.data.mapper.toEntityList
import com.example.myapplication.data.mapper.toModelList
import com.example.myapplication.data.model.ImageItem
import com.example.myapplication.data.model.ImagesResponse
import com.example.myapplication.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ImageRepository(
    private val apiService: ApiService,
    private val imageDao: ImageDao
) {
    
    suspend fun getImages(
        token: String,
        startDate: String? = null,
        endDate: String? = null,
        page: Int = 1
    ): Result<ImagesResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getImages(
                    token = "Bearer $token",
                    startDate = startDate,
                    endDate = endDate,
                    page = page
                )
                if (response.isSuccessful && response.body() != null) {
                    val imagesResponse = response.body()!!
                    // Кэшируем изображения в БД
                    if (page == 1) {
                        // Очищаем старый кэш только для первой страницы
                        imageDao.deleteAllImages()
                    }
                    imageDao.insertImages(imagesResponse.images.toEntityList())
                    Result.success(imagesResponse)
                } else {
                    Result.failure(Exception("Failed to fetch images: ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // Получение изображений из локальной БД
    fun getLocalImages(): Flow<List<ImageItem>> {
        return imageDao.getAllImages().map { it.toModelList() }
    }
    
    fun getLocalImagesByDateRange(startDate: String?, endDate: String?): Flow<List<ImageItem>> {
        return when {
            startDate != null && endDate != null -> {
                imageDao.getImagesByDateRange(startDate, endDate).map { it.toModelList() }
            }
            startDate != null -> {
                imageDao.getImagesFromDate(startDate).map { it.toModelList() }
            }
            endDate != null -> {
                imageDao.getImagesUntilDate(endDate).map { it.toModelList() }
            }
            else -> {
                imageDao.getAllImages().map { it.toModelList() }
            }
        }
    }
    
    suspend fun clearOldCache(daysToKeep: Int = 7) {
        withContext(Dispatchers.IO) {
            val cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L)
            imageDao.deleteOldCache(cutoffTime)
        }
    }
}
