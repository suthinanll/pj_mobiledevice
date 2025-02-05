package com.example.ass07


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
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
            MyPet(navController)
        }
        composable(route = Screen.Profile.route) {
            Profile()
        }
        composable(route = Screen.Mypetinsert.route) {
           Mypetinsert(navController)
        }
        composable(route = Screen.Mypetedit.route + "/{petId}") { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId")?.toIntOrNull()
            val petViewModel: PetViewModel = viewModel()
            val pet by petViewModel.pet.observeAsState()

            LaunchedEffect(petId) {
                petId?.let { petViewModel.loadPet(it) }
            }

            pet?.let { Mypetedit(navController, it) }
        }
    }
}