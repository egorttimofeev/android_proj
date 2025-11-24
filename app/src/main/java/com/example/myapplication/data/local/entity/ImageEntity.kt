package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey
    val id: Int,
    val url: String,
    val title: String?,
    val description: String?,
    val createdAt: String,
    val thumbnailUrl: String?,
    val cachedAt: Long = System.currentTimeMillis()
)
