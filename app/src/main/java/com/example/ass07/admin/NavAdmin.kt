package com.example.ass07.admin


import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ass07.ManageRoom
import com.example.ass07.RoomList
import com.example.ass07.customer.Booking

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
            Booking()
        }
        composable(route = ScreenAdmin.PetsAdmin.route) {
            PetsAdmin()
        }
        composable(
            route = "room_list/{roomType}/{petType}",
            arguments = listOf(
                navArgument("roomType") { type = NavType.StringType },
                navArgument("petType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomType = backStackEntry.arguments?.getString("roomType") ?: "ทั้งหมด"
            val petType = backStackEntry.arguments?.getString("petType") ?: "ทั้งหมด"

            RoomList(roomType = roomType, petType = petType)
        }

    }
}
