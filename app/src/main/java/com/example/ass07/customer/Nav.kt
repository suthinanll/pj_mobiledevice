package com.example.ass07.customer


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ass07.admin.ManageRoom
import com.example.ass07.admin.PetsAdmin
import com.example.ass07.admin.RoomEdit
import com.example.ass07.admin.ScreenAdmin
import com.example.ass07.customer.LoginRegister.Login
import com.example.ass07.customer.LoginRegister.Register
import com.example.ass07.customer.LoginRegister.ScreenLogin
import com.example.ass07.customer.Mypet.MyPet
import com.example.ass07.customer.Mypet.Mypetedit
import com.example.ass07.customer.Mypet.Mypetinsert
import com.example.ass07.customer.Mypet.PetViewModel
import com.example.ass07.customer.Profile.Profile

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenLogin.Login.route
    ) {
        composable(route = ScreenLogin.Login.route) {
            Login(navController)
        }
        composable(route = ScreenLogin.Register.route) {
            Register(navController)
        }
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
            Profile(navController)
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