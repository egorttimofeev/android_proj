package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.repository.RoomRepository
import com.example.myapplication.ui.rooms.RoomDetailViewModel

class RoomDetailViewModelFactory(
    private val roomRepository: RoomRepository,
    private val roomId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoomDetailViewModel(roomRepository, roomId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
