package com.example.myapplication.data.local.dao

import androidx.room.*
import com.example.myapplication.data.local.entity.RoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    
    @Query("SELECT * FROM rooms ORDER BY id ASC")
    fun getAllRooms(): Flow<List<RoomEntity>>
    
    @Query("SELECT * FROM rooms ORDER BY id ASC")
    suspend fun getRoomsSync(): List<RoomEntity>
    
    @Query("SELECT * FROM rooms WHERE status = :status ORDER BY id ASC")
    fun getRoomsByStatus(status: String): Flow<List<RoomEntity>>
    
    @Query("SELECT * FROM rooms WHERE id = :roomId")
    suspend fun getRoomById(roomId: Int): RoomEntity?
    
    @Query("SELECT * FROM rooms WHERE id = :roomId")
    fun getRoomByIdFlow(roomId: Int): Flow<RoomEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: RoomEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRooms(rooms: List<RoomEntity>)
    
    @Update
    suspend fun updateRoom(room: RoomEntity)
    
    @Query("DELETE FROM rooms")
    suspend fun deleteAllRooms()
    
    @Query("DELETE FROM rooms WHERE id = :roomId")
    suspend fun deleteRoom(roomId: Int)
}
