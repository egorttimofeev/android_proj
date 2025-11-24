package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.DatabaseSeeder
import com.example.myapplication.data.local.UserPreferences
import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.repository.AuthRepository
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.navigation.AppNavigation
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    
    val database = remember { AppDatabase.getDatabase(context) }
    val userPreferences = remember { UserPreferences(context) }
    
    val authRepository = remember { 
        AuthRepository(RetrofitClient.apiService, database.userDao()) 
    }
    val roomRepository = remember { 
        com.example.myapplication.data.repository.RoomRepository(
            database.roomDao(),
            database.bookingDao()
        ) 
    }
    
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository, userPreferences)
    )
    
    var startDestination by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        // Инициализируем БД комнат (пока только комнаты)
        val roomDao = database.roomDao()
        val seeder = DatabaseSeeder(context, database)

        val hasRooms = withContext(kotlinx.coroutines.Dispatchers.IO) {
            roomDao.getAllRooms().first().isNotEmpty()
        }

        if (!hasRooms) {
            seeder.seedRooms()
            seeder.seedBookings()
        }

        // Всегда показываем экран входа при старте (пользователь должен явно войти)
        startDestination = Screen.Login.route
    }
    
    startDestination?.let { destination ->
        AppNavigation(
            navController = navController,
            authViewModel = authViewModel,
            roomRepository = roomRepository,
            startDestination = destination
        )
    }
}