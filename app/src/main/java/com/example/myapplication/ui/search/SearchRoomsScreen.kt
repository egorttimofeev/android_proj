package com.example.myapplication.ui.search

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.model.Room
import com.example.myapplication.ui.rooms.RoomsState
import com.example.myapplication.ui.rooms.RoomsViewModel
import com.example.myapplication.ui.rooms.RoomCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchRoomsScreen(
    onSearchClick: (String, String, Int) -> Unit,
    onBackClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    var checkInDate by rememberSaveable { mutableStateOf("") }
    var checkOutDate by rememberSaveable { mutableStateOf("") }
    var guestCount by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поиск номеров") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Выйти")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
                                    cal.set(y, m, d)
                                    checkInDate = dateFormat.format(cal.time)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            }
                    ) {
                        OutlinedTextField(
                            value = checkInDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Дата заезда") },
                            placeholder = { Text("дд.мм.гггг") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int ->
                                    cal.set(y, m, d)
                                    checkOutDate = dateFormat.format(cal.time)
                                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
                            }
                    ) {
                        OutlinedTextField(
                            value = checkOutDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Дата выезда") },
                            placeholder = { Text("дд.мм.гггг") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = guestCount,
                        onValueChange = { if (it.isEmpty() || it.all { ch -> ch.isDigit() }) guestCount = it },
                        label = { Text("Количество гостей") },
                        placeholder = { Text("1") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(
                            onClick = { 
                                checkInDate = ""
                                checkOutDate = ""
                                guestCount = ""
                            }, 
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) { 
                            Text("назад") 
                        }
                        Button(
                            onClick = {
                                if (checkInDate.isNotEmpty() && checkOutDate.isNotEmpty() && guestCount.isNotEmpty()) {
                                    val capacity = guestCount.toIntOrNull() ?: 1
                                    onSearchClick(checkInDate, checkOutDate, capacity)
                                }
                            }
                        ) { 
                            Text("Найти") 
                        }
                    }
                }
            }
        }
    }
}
