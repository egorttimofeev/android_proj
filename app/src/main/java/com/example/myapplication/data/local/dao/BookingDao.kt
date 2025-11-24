package com.example.myapplication.data.local.dao

import androidx.room.*
import com.example.myapplication.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    
    @Query("SELECT * FROM bookings WHERE roomId = :roomId")
    fun getBookingsForRoom(roomId: Int): Flow<List<BookingEntity>>
    
    @Query("""
        SELECT * FROM bookings 
        WHERE roomId = :roomId 
        AND (
            (checkInDate <= :checkOut AND checkOutDate >= :checkIn)
        )
    """)
    suspend fun getConflictingBookings(roomId: Int, checkIn: String, checkOut: String): List<BookingEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity)
    
    @Query("DELETE FROM bookings")
    suspend fun deleteAllBookings()
}
