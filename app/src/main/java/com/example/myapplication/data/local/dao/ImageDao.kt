package com.example.myapplication.data.local.dao

import androidx.room.*
import com.example.myapplication.data.local.entity.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {
    
    @Query("SELECT * FROM images ORDER BY createdAt DESC")
    fun getAllImages(): Flow<List<ImageEntity>>
    
    @Query("SELECT * FROM images WHERE createdAt >= :startDate AND createdAt <= :endDate ORDER BY createdAt DESC")
    fun getImagesByDateRange(startDate: String, endDate: String): Flow<List<ImageEntity>>
    
    @Query("SELECT * FROM images WHERE createdAt >= :startDate ORDER BY createdAt DESC")
    fun getImagesFromDate(startDate: String): Flow<List<ImageEntity>>
    
    @Query("SELECT * FROM images WHERE createdAt <= :endDate ORDER BY createdAt DESC")
    fun getImagesUntilDate(endDate: String): Flow<List<ImageEntity>>
    
    @Query("SELECT * FROM images WHERE id = :imageId")
    suspend fun getImageById(imageId: Int): ImageEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<ImageEntity>)
    
    @Update
    suspend fun updateImage(image: ImageEntity)
    
    @Query("DELETE FROM images")
    suspend fun deleteAllImages()
    
    @Query("DELETE FROM images WHERE id = :imageId")
    suspend fun deleteImage(imageId: Int)
    
    @Query("DELETE FROM images WHERE cachedAt < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
}
