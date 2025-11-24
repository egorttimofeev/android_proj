package com.example.myapplication.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object MainMenu : Screen("main_menu")
    object SearchRooms : Screen("search_rooms")
    object SearchResults : Screen("search_results/{checkIn}/{checkOut}/{guestCount}") {
        fun createRoute(checkIn: String, checkOut: String, guestCount: Int) = 
            "search_results/$checkIn/$checkOut/$guestCount"
    }
    object AllRooms : Screen("all_rooms")
    object RoomDetail : Screen("room_detail/{roomId}") {
        fun createRoute(roomId: Int) = "room_detail/$roomId"
    }
}
