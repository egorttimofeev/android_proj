package com.example.myapplication.data.model

enum class RoomStatus {
    AVAILABLE,    // Доступен
    UNAVAILABLE   // Недоступен
}

data class Room(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val capacity: Int,       // Вместимость (количество гостей)
    val price: Double,       // Цена
    val amenities: String,   // Удобства (можно через запятую)
    val status: RoomStatus,
    val createdAt: String
)
