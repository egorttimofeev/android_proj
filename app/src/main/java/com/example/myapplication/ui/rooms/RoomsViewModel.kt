package com.example.myapplication.ui.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.model.Room
import com.example.myapplication.data.model.RoomStatus
import com.example.myapplication.data.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Состояния экрана списка номеров.
 */
sealed class RoomsState {
    object Loading : RoomsState()
    data class Success(val rooms: List<Room>) : RoomsState()
    data class Error(val message: String) : RoomsState()
}

/**
 * Фильтры для отображения номеров.
 */
enum class RoomFilter {
    ALL,
    AVAILABLE,
    UNAVAILABLE
}

/**
 * ViewModel для управления списком номеров и их фильтрацией.
 *
 * @property roomRepository репозиторий для работы с номерами
 * @property userPreferences настройки пользователя для работы с токеном авторизации
 * @property autoLoadRooms автоматически загружать номера при создании ViewModel
 */
class RoomsViewModel(
    private val roomRepository: RoomRepository,
    private val userPreferences: UserPreferences,
    autoLoadRooms: Boolean = true
) : ViewModel() {
    
    private val _roomsState = MutableStateFlow<RoomsState>(RoomsState.Loading)
    val roomsState: StateFlow<RoomsState> = _roomsState.asStateFlow()
    
    private val _currentFilter = MutableStateFlow(RoomFilter.ALL)
    val currentFilter: StateFlow<RoomFilter> = _currentFilter.asStateFlow()
    
    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut.asStateFlow()
    
    init {
        if (autoLoadRooms) {
            loadRooms()
        }
    }
    
    fun setFilter(filter: RoomFilter) {
        _currentFilter.value = filter
        loadRooms()
    }
    
    fun loadAllRooms() {
        _currentFilter.value = RoomFilter.ALL
        loadRooms()
    }
    
    /**
     * Фильтрует номера по вместимости (не используется в текущей версии).
     */
    fun filterByCapacity(capacity: Int) {
        viewModelScope.launch {
            _roomsState.value = RoomsState.Loading
            
            try {
                roomRepository.getAllRooms().collect { rooms ->
                    val filtered = rooms.filter { 
                        it.capacity >= capacity && it.status == RoomStatus.AVAILABLE 
                    }
                    _roomsState.value = RoomsState.Success(filtered)
                }
            } catch (e: Exception) {
                _roomsState.value = RoomsState.Error(e.message ?: "Ошибка загрузки номеров")
            }
        }
    }

    /**
     * Фильтрует номера по датам и вместимости.
     * Используется на экране результатов поиска.
     *
     * @param checkInDate дата заезда в формате yyyy-MM-dd
     * @param checkOutDate дата выезда в формате yyyy-MM-dd
     * @param guestCount количество гостей
     */
    fun filterByDatesAndCapacity(checkInDate: String, checkOutDate: String, guestCount: Int) {
        viewModelScope.launch {
            _roomsState.value = RoomsState.Loading
            
            try {
                val available = roomRepository.getAvailableRoomsByDatesAndCapacity(
                    checkInDate, checkOutDate, guestCount
                )
                _roomsState.value = RoomsState.Success(available)
            } catch (e: Exception) {
                _roomsState.value = RoomsState.Error(e.message ?: "Ошибка загрузки номеров")
            }
        }
    }
    
    private fun loadRooms() {
        viewModelScope.launch {
            _roomsState.value = RoomsState.Loading
            
            try {
                when (_currentFilter.value) {
                    RoomFilter.ALL -> {
                        roomRepository.getAllRooms().collect { rooms ->
                            _roomsState.value = RoomsState.Success(rooms)
                        }
                    }
                    RoomFilter.AVAILABLE -> {
                        roomRepository.getRoomsByStatus(RoomStatus.AVAILABLE).collect { rooms ->
                            _roomsState.value = RoomsState.Success(rooms)
                        }
                    }
                    RoomFilter.UNAVAILABLE -> {
                        roomRepository.getRoomsByStatus(RoomStatus.UNAVAILABLE).collect { rooms ->
                            _roomsState.value = RoomsState.Success(rooms)
                        }
                    }
                }
            } catch (e: Exception) {
                _roomsState.value = RoomsState.Error(e.message ?: "Ошибка загрузки номеров")
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            userPreferences.clearAuthData()
            _isLoggedOut.value = true
        }
    }
}
