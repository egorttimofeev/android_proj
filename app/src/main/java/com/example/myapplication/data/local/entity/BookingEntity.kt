package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность бронирования номера в базе данных.
 * Хранит информацию о забронированном номере, датах заезда/выезда и гостях.
 *
 * @property id уникальный идентификатор бронирования
 * @property roomId идентификатор забронированного номера
 * @property checkInDate дата заезда в формате yyyy-MM-dd
 * @property checkOutDate дата выезда в формате yyyy-MM-dd
 * @property guestName имя гостя
 * @property guestCount количество гостей
 */
@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val roomId: Int,
    val checkInDate: String,
    val checkOutDate: String,
    val guestName: String,
    val guestCount: Int
)
