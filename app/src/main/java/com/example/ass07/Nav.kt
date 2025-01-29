package com.example.ass07


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

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
            MyPet()
        }
        composable(route = Screen.Profile.route) {
            Profile()
        }
    }
}