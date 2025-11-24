package com.example.myapplication.ui.rooms

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.myapplication.data.model.Room
import com.example.myapplication.data.model.RoomStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomsListScreen(
    viewModel: RoomsViewModel,
    onRoomClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    showFilters: Boolean = true
) {
    val roomsState by viewModel.roomsState.collectAsStateWithLifecycle()
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val isLoggedOut by viewModel.isLoggedOut.collectAsStateWithLifecycle()
    
    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            onLogout()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showFilters) "Поиск номеров" else "Все номера") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Выйти"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Фильтры (только на экране поиска)
            if (showFilters) {
                FilterRow(
                    currentFilter = currentFilter,
                    onFilterChange = { viewModel.setFilter(it) }
                )
            }
            
            // Список номеров
            when (val state = roomsState) {
                is RoomsState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is RoomsState.Success -> {
                    if (state.rooms.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Нет номеров")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterRow(
    currentFilter: RoomFilter,
    onFilterChange: (RoomFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentFilter == RoomFilter.ALL,
            onClick = { onFilterChange(RoomFilter.ALL) },
            label = { Text("Все") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = currentFilter == RoomFilter.AVAILABLE,
            onClick = { onFilterChange(RoomFilter.AVAILABLE) },
            label = { Text("Доступны") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = currentFilter == RoomFilter.UNAVAILABLE,
            onClick = { onFilterChange(RoomFilter.UNAVAILABLE) },
            label = { Text("Недоступны") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun RoomCard(
    room: Room,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Фото номера
            AsyncImage(
                model = coil.request.ImageRequest.Builder(LocalContext.current)
                    .data("file:///android_asset/${room.imageUrl}")
                    .crossfade(true)
                    .build(),
                contentDescription = room.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(android.R.drawable.ic_menu_report_image),
                placeholder = painterResource(android.R.drawable.ic_menu_gallery)
            )
            
            // Информация о номере
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = room.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Статус
                    Surface(
                        color = if (room.status == RoomStatus.AVAILABLE)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = if (room.status == RoomStatus.AVAILABLE) "Доступен" else "Недоступен",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (room.status == RoomStatus.AVAILABLE)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = room.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Вместимость: ${room.capacity} чел.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${room.price.toInt()} ₽/сутки",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
