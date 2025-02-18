package com.example.ass07.admin


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ass07.customer.Booking
import com.example.ass07.customer.LoginRegister.Login
import com.example.ass07.customer.LoginRegister.ScreenLogin
import com.example.ass07.customer.Mypet.Mypetinsert
import com.example.ass07.customer.Screen

@Composable
fun NavGraphAdmin(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenAdmin.ManageRoom.route
    ) {
        composable(route = ScreenLogin.Login.route) {
            Login(navController)
        }
        composable(route = ScreenAdmin.ManageRoom.route) {
            ManageRoom(navController)
        }
        composable(route = ScreenAdmin.Booking.route) {
            Booking()
        }
        composable(route = ScreenAdmin.PetsAdmin.route) {
            PetsAdmin()
        }
        composable(route = ScreenAdmin.RoomInsert.route) {
            RoomInsert(navController)
        }



    }
}