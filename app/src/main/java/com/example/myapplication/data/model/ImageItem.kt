package com.example.myapplication.data.model

data class ImageItem(
    val id: Int,
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val createdAt: String, // ISO 8601 date format
    val thumbnailUrl: String? = null
)
