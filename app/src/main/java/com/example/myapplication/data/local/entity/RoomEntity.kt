package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность номера отеля в базе данных.
 *
 * @property id уникальный идентификатор номера
 * @property name название номера
 * @property description описание номера
 * @property imageUrl URL изображения номера (file:///android_asset/photos/...)
 * @property capacity вместимость номера (количество гостей)
 * @property price цена за ночь в рублях
 * @property amenities список удобств через запятую
 * @property status статус номера (AVAILABLE или UNAVAILABLE)
 * @property createdAt дата создания записи
 * @property cachedAt время кеширования записи
 */
@Entity(tableName = "rooms")
data class RoomEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String,
    val capacity: Int,
    val price: Double,
    val amenities: String,
    val status: String,
    val createdAt: String,
    val cachedAt: Long = System.currentTimeMillis()
)
