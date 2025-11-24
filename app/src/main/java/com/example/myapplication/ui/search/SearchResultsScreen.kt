package com.example.myapplication.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.ui.rooms.RoomsState
import com.example.myapplication.ui.rooms.RoomsViewModel
import com.example.myapplication.ui.rooms.RoomCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    viewModel: RoomsViewModel,
    checkInDate: String,
    checkOutDate: String,
    guestCount: Int,
    onRoomClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {
    val roomsState by viewModel.roomsState.collectAsStateWithLifecycle()
    val isLoggedOut by viewModel.isLoggedOut.collectAsStateWithLifecycle()

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) onLogout()
    }

    LaunchedEffect(Unit) {
        // Конвертируем даты из формата dd.MM.yyyy в yyyy-MM-dd для БД
        val inputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dbFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val checkInDateDb = try {
            val date = inputFormat.parse(checkInDate)
            if (date != null) dbFormat.format(date) else checkInDate
        } catch (e: Exception) {
            checkInDate
        }
        
        val checkOutDateDb = try {
            val date = inputFormat.parse(checkOutDate)
            if (date != null) dbFormat.format(date) else checkOutDate
        } catch (e: Exception) {
            checkOutDate
        }
        
        viewModel.filterByDatesAndCapacity(checkInDateDb, checkOutDateDb, guestCount)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результаты поиска") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Выйти")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = roomsState) {
            is RoomsState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RoomsState.Success -> {
                if (state.rooms.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Номера не найдены для выбранных параметров",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.rooms) { room ->
                            RoomCard(
                                room = room,
                                onClick = { onRoomClick(room.id) }
                            )
                        }
                    }
                }
            }
            is RoomsState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
