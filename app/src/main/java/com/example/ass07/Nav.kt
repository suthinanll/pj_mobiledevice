package com.example.ass07


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.History.route) {
            History()
        }
        composable(route = Screen.Home.route) {
            Home()
        }
        composable(route = Screen.MyPet.route) {
            MyPet(navController)
        }
        composable(route = Screen.Profile.route) {
            Profile()
        }
        composable(route = Screen.Mypetinsert.route) {
           Mypetinsert(navController)
        }
        composable(
            route = Screen.BookingDetailsScreen.route + "/{bookingId}",
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailsScreen(bookingId) // ส่งค่าไป
        }

    }
}