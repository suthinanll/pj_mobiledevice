package com.example.ass07.admin


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ass07.ManageRoom

@Composable
fun NavGraphAdmin(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenAdmin.ManageRoom.route
    ) {

        composable(route = ScreenAdmin.ManageRoom.route) {
            ManageRoom()
        }
        composable(route = ScreenAdmin.Booking.route) {
            Booking()
        }
        composable(route = ScreenAdmin.PetsAdmin.route) {
            PetsAdmin()
        }

        composable(route = ScreenAdmin.BookingDetail.route) {
            BookingDetail()
        }

    }
}