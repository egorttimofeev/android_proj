package com.example.myapplication.ui.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Room
import com.example.myapplication.data.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RoomDetailState {
    object Loading : RoomDetailState()
    data class Success(val room: Room) : RoomDetailState()
    data class Error(val message: String) : RoomDetailState()
}

class RoomDetailViewModel(
    private val roomRepository: RoomRepository,
    private val roomId: Int
) : ViewModel() {
    
    private val _roomState = MutableStateFlow<RoomDetailState>(RoomDetailState.Loading)
    val roomState: StateFlow<RoomDetailState> = _roomState.asStateFlow()
    
    init {
        loadRoom()
    }
    
    private fun loadRoom() {
        viewModelScope.launch {
            _roomState.value = RoomDetailState.Loading
            
            try {
                roomRepository.getRoomById(roomId).collect { room ->
                    if (room != null) {
                        _roomState.value = RoomDetailState.Success(room)
                    } else {
                        _roomState.value = RoomDetailState.Error("Номер не найден")
                    }
                }
            } catch (e: Exception) {
                _roomState.value = RoomDetailState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }
}
