package com.example.ass07.admin


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ass07.admin.booking.Booking
import com.example.ass07.admin.booking.BookingDetail

@Composable
fun NavGraphAdmin(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenAdmin.ManageRoom.route
    ) {

        composable(route = ScreenAdmin.ManageRoom.route) {
            ManageRoom(navController)
        }
        composable(route = ScreenAdmin.Booking.route) {
            Booking(navController)
        }
        composable(route = ScreenAdmin.PetsAdmin.route) {
            PetsAdmin()
        }
        composable(route = ScreenAdmin.RoomInsert.route) {
            RoomInsert(navController)
        }

        composable("booking_detail/{id}") { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            BookingDetail(bookingId)
        }

    }
}
