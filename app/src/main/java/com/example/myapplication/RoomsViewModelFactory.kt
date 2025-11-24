package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.repository.RoomRepository
import com.example.myapplication.ui.rooms.RoomsViewModel

class RoomsViewModelFactory(
    private val roomRepository: RoomRepository,
    private val userPreferences: UserPreferences,
    private val autoLoadRooms: Boolean = true
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomsViewModel(roomRepository, userPreferences, autoLoadRooms) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
