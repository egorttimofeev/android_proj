package com.example.myapplication.ui.rooms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.myapplication.data.model.RoomStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    viewModel: RoomDetailViewModel,
    onBackClick: () -> Unit
) {
    val roomState by viewModel.roomState.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали номера") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = roomState) {
            is RoomDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RoomDetailState.Success -> {
                val room = state.room
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Фото номера
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/${room.imageUrl}")
                            .crossfade(true)
                            .build(),
                        contentDescription = room.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Crop,
                        error = painterResource(android.R.drawable.ic_menu_report_image),
                        placeholder = painterResource(android.R.drawable.ic_menu_gallery)
                    )
                    
                    // Информация о номере
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Название
                        Text(
                            text = room.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
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
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = if (room.status == RoomStatus.AVAILABLE)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Описание
                        Text(
                            text = "Описание",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = room.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Вместимость
                        InfoRow(
                            label = "Вместимость",
                            value = "${room.capacity} человек(а)"
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Цена
                        InfoRow(
                            label = "Цена",
                            value = "${room.price.toInt()} ₽/сутки"
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Divider()
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Удобства
                        Text(
                            text = "Удобства",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val amenitiesList = room.amenities.split(",").map { it.trim() }
                        amenitiesList.forEach { amenity ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = amenity,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
            }
            is RoomDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
