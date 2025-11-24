package com.example.myapplication.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.RoomsViewModelFactory
import com.example.myapplication.RoomDetailViewModelFactory
import com.example.myapplication.data.repository.RoomRepository
import com.example.myapplication.ui.auth.AuthViewModel
import com.example.myapplication.ui.auth.LoginScreen
import com.example.myapplication.ui.auth.RegisterScreen
import com.example.myapplication.ui.menu.MainMenuScreen
import com.example.myapplication.ui.rooms.RoomsListScreen
import com.example.myapplication.ui.rooms.RoomsViewModel
import com.example.myapplication.ui.rooms.RoomDetailScreen
import com.example.myapplication.ui.rooms.RoomDetailViewModel
import com.example.myapplication.ui.rooms.RoomFilter
import com.example.myapplication.ui.search.SearchRoomsScreen
import com.example.myapplication.ui.search.SearchResultsScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    roomRepository: RoomRepository,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Screen.MainMenu.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.MainMenu.route) {
            val username by authViewModel.userPreferences.username.collectAsStateWithLifecycle(initialValue = "")
            MainMenuScreen(
                onSearchWithFilters = {
                    navController.navigate(Screen.SearchRooms.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onViewAllRooms = {
                    navController.navigate(Screen.AllRooms.route) {
                        popUpTo(Screen.MainMenu.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                username = username ?: ""
            )
        }
        
        composable(Screen.SearchRooms.route) {
            SearchRoomsScreen(
                onSearchClick = { checkIn, checkOut, guests ->
                    val encodedCheckIn = URLEncoder.encode(checkIn, StandardCharsets.UTF_8.toString())
                    val encodedCheckOut = URLEncoder.encode(checkOut, StandardCharsets.UTF_8.toString())
                    navController.navigate(Screen.SearchResults.createRoute(encodedCheckIn, encodedCheckOut, guests))
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.SearchResults.route,
            arguments = listOf(
                navArgument("checkIn") { type = NavType.StringType },
                navArgument("checkOut") { type = NavType.StringType },
                navArgument("guestCount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val checkIn = backStackEntry.arguments?.getString("checkIn") ?: return@composable
            val checkOut = backStackEntry.arguments?.getString("checkOut") ?: return@composable
            val guestCount = backStackEntry.arguments?.getInt("guestCount") ?: 1
            
            val viewModel: RoomsViewModel = viewModel(
                factory = RoomsViewModelFactory(
                    roomRepository = roomRepository,
                    userPreferences = authViewModel.userPreferences,
                    autoLoadRooms = false
                )
            )
            SearchResultsScreen(
                viewModel = viewModel,
                checkInDate = checkIn,
                checkOutDate = checkOut,
                guestCount = guestCount,
                onRoomClick = { roomId ->
                    navController.navigate(Screen.RoomDetail.createRoute(roomId))
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(Screen.AllRooms.route) {
            val viewModel: RoomsViewModel = viewModel(
                factory = RoomsViewModelFactory(
                    roomRepository = roomRepository,
                    userPreferences = authViewModel.userPreferences
                )
            )
            // Устанавливаем фильтр "Все" при входе
            LaunchedEffect(Unit) {
                viewModel.setFilter(RoomFilter.ALL)
            }
            RoomsListScreen(
                viewModel = viewModel,
                onRoomClick = { roomId ->
                    navController.navigate(Screen.RoomDetail.createRoute(roomId))
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                showFilters = true
            )
        }
        
        composable(
            route = Screen.RoomDetail.route,
            arguments = listOf(
                navArgument("roomId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getInt("roomId") ?: return@composable
            val viewModel: RoomDetailViewModel = viewModel(
                factory = RoomDetailViewModelFactory(
                    roomRepository = roomRepository,
                    roomId = roomId
                )
            )
            RoomDetailScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
