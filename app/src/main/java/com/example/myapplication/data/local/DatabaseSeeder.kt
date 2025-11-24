package com.example.myapplication.data.local

import android.content.Context
import com.example.myapplication.data.local.entity.BookingEntity
import com.example.myapplication.data.local.entity.ImageEntity
import com.example.myapplication.data.local.entity.RoomEntity
import com.example.myapplication.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Класс для заполнения базы данных тестовыми данными при первом запуске приложения.
 * Создает пользователей, изображения, номера отеля и бронирования.
 */
class DatabaseSeeder(private val context: Context, private val database: AppDatabase) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    
    /**
     * Заполняет БД тестовыми пользователями и изображениями из assets.
     */
    suspend fun seedDatabase(context: Context) = withContext(Dispatchers.IO) {
        val userDao = database.userDao()
        val imageDao = database.imageDao()
        
        // Очищаем существующие данные
        userDao.deleteAllUsers()
        imageDao.deleteAllImages()
        
        // Добавляем тестовых пользователей
        val testUsers = listOf(
            UserEntity(
                id = 1,
                username = "1",
                email = "user1@test.com",
                token = "test_token_1",
                password = "1"
            ),
            UserEntity(
                id = 2,
                username = "2",
                email = "user2@test.com",
                token = "test_token_2",
                password = "2"
            )
        )
        
        testUsers.forEach { userDao.insertUser(it) }
        
        // Добавляем тестовые изображения из папки photos
        val calendar = Calendar.getInstance()
        val testImages = listOf(
            ImageEntity(
                id = 1,
                url = "photos/1267_room-type.jpg",
                title = "Тестовое изображение 1",
                description = "Описание изображения 1",
                createdAt = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, -7) }.time),
                thumbnailUrl = "photos/1267_room-type.jpg"
            ),
            ImageEntity(
                id = 2,
                url = "photos/tipy-nomerov.jpg",
                title = "Тестовое изображение 2",
                description = "Описание изображения 2",
                createdAt = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time),
                thumbnailUrl = "photos/tipy-nomerov.jpg"
            ),
            ImageEntity(
                id = 3,
                url = "photos/images-2.jpeg",
                title = "Тестовое изображение 3",
                description = "Описание изображения 3",
                createdAt = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time),
                thumbnailUrl = "photos/images-2.jpeg"
            ),
            ImageEntity(
                id = 4,
                url = "photos/r3.jpg",
                title = "Тестовое изображение 4",
                description = "Описание изображения 4",
                createdAt = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time),
                thumbnailUrl = "photos/r3.jpg"
            ),
            ImageEntity(
                id = 5,
                url = "photos/images.jpeg",
                title = "Тестовое изображение 5",
                description = "Описание изображения 5",
                createdAt = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time),
                thumbnailUrl = "photos/images.jpeg"
            ),
            ImageEntity(
                id = 6,
                url = "photos/luxury-room.jpg",
                title = "Тестовое изображение 6",
                description = "Описание изображения 6",
                createdAt = dateFormat.format(calendar.apply { add(Calendar.DAY_OF_MONTH, 1) }.time),
                thumbnailUrl = "photos/luxury-room.jpg"
            ),
            ImageEntity(
                id = 7,
                url = "photos/standard-room.jpg",
                title = "Тестовое изображение 7",
                description = "Описание изображения 7",
                createdAt = dateFormat.format(Calendar.getInstance().time),
                thumbnailUrl = "photos/standard-room.jpg"
            )
        )
        
        imageDao.insertImages(testImages)
    }
    
    /**
     * Создает номера отеля на основе фотографий из папки assets/photos.
     * Каждому номеру присваивается вместимость, цена, удобства и статус доступности.
     */
    suspend fun seedRooms() = withContext(Dispatchers.IO) {
        val roomDao = database.roomDao()
        
        // Очищаем существующие номера
        roomDao.deleteAllRooms()
        
        // Явный список файлов в assets/photos для предсказуемого сопоставления с номерами
        val photoFiles = listOf(
            "1267_room-type.jpg",
            "images-2.jpeg",
            "images.jpeg",
            "luxury-room.jpg",
            "r3.jpg",
            "standard-room.jpg",
            "tipy-nomerov.jpg"
        )
        
        // Создаем номера на основе фотографий
        val rooms = photoFiles.mapIndexed { index, photoFile ->
            val roomNumber = index + 1
            val isAvailable = roomNumber % 3 != 0 // Каждый третий номер недоступен
            
            RoomEntity(
                id = roomNumber,
                name = "Номер $roomNumber",
                description = "Комфортабельный номер с прекрасным видом. Включает все необходимые удобства для приятного отдыха.",
                imageUrl = "photos/$photoFile",
                capacity = when(roomNumber % 4) {
                    0 -> 4
                    1 -> 1
                    2 -> 2
                    else -> 3
                },
                price = 2000.0 + (roomNumber * 500.0),
                amenities = when(roomNumber % 3) {
                    0 -> "Wi-Fi, Телевизор, Кондиционер, Мини-бар"
                    1 -> "Wi-Fi, Телевизор, Кондиционер"
                    else -> "Wi-Fi, Телевизор"
                },
                status = if (isAvailable) "AVAILABLE" else "UNAVAILABLE",
                createdAt = dateFormat.format(System.currentTimeMillis())
            )
        }
        
        roomDao.insertRooms(rooms)
    }

    /**
     * Создает тестовые бронирования для демонстрации фильтрации по датам.
     * Номер 1 занят 25-27 ноября и 30 ноября - 2 декабря 2025.
     * Номер 2 занят 26-28 ноября 2025.
     */
    suspend fun seedBookings() = withContext(Dispatchers.IO) {
        val bookingDao = database.bookingDao()
        bookingDao.deleteAllBookings()

        // Добавляем несколько тестовых бронирований
        val testBookings = listOf(
            BookingEntity(
                id = 1,
                roomId = 1,
                checkInDate = "2025-11-25",
                checkOutDate = "2025-11-27",
                guestName = "Иванов И.И.",
                guestCount = 2
            ),
            BookingEntity(
                id = 2,
                roomId = 2,
                checkInDate = "2025-11-26",
                checkOutDate = "2025-11-28",
                guestName = "Петров П.П.",
                guestCount = 1
            ),
            BookingEntity(
                id = 3,
                roomId = 1,
                checkInDate = "2025-11-30",
                checkOutDate = "2025-12-02",
                guestName = "Сидоров С.С.",
                guestCount = 2
            )
        )

        testBookings.forEach { bookingDao.insertBooking(it) }
    }
}
