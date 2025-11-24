package com.example.myapplication.data.repository

import com.example.myapplication.data.local.dao.BookingDao
import com.example.myapplication.data.local.dao.RoomDao
import com.example.myapplication.data.mapper.toRoomModelList
import com.example.myapplication.data.mapper.toModel
import com.example.myapplication.data.model.Room
import com.example.myapplication.data.model.RoomStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Репозиторий для работы с данными о номерах отеля.
 * Предоставляет методы для получения, фильтрации и поиска номеров.
 *
 * @property roomDao DAO для работы с таблицей номеров
 * @property bookingDao DAO для работы с таблицей бронирований (опционально)
 */
class RoomRepository(private val roomDao: RoomDao, private val bookingDao: BookingDao? = null) {
    
    fun getAllRooms(): Flow<List<Room>> {
        return roomDao.getAllRooms().map { it.toRoomModelList() }
    }
    
    fun getRoomsByStatus(status: RoomStatus): Flow<List<Room>> {
        return roomDao.getRoomsByStatus(status.name).map { it.toRoomModelList() }
    }
    
    fun getRoomById(roomId: Int): Flow<Room?> {
        return roomDao.getRoomByIdFlow(roomId).map { entity ->
            entity?.toModel()
        }
    }

    /**
     * Возвращает список доступных номеров, соответствующих критериям поиска.
     * Фильтрует номера по вместимости, статусу доступности и отсутствию конфликтов бронирований.
     *
     * @param checkInDate дата заезда в формате yyyy-MM-dd
     * @param checkOutDate дата выезда в формате yyyy-MM-dd
     * @param minCapacity минимальная вместимость номера
     * @return список доступных номеров
     */
    suspend fun getAvailableRoomsByDatesAndCapacity(
        checkInDate: String,
        checkOutDate: String,
        minCapacity: Int
    ): List<Room> {
        val allRoomsEntities = roomDao.getRoomsSync()
        val allRooms = allRoomsEntities.toRoomModelList()
        
        return allRooms.filter { room ->
            val meetsCapacity = room.capacity >= minCapacity
            val isAvailable = room.status == RoomStatus.AVAILABLE
            val hasNoConflicts = bookingDao?.getConflictingBookings(room.id, checkInDate, checkOutDate)?.isEmpty() ?: true
            
            meetsCapacity && isAvailable && hasNoConflicts
        }
    }
}
