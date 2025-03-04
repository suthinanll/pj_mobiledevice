package com.example.ass07.customer


import BookingScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ass07.RoomEditType
import com.example.ass07.RoomList
import com.example.ass07.admin.AdminDashboard
import com.example.ass07.admin.ManageRoom
import com.example.ass07.admin.PetsAdmin
import com.example.ass07.admin.RoomEdit
import com.example.ass07.admin.RoomInsert
import com.example.ass07.admin.ScreenAdmin
import com.example.ass07.admin.booking.BookingDetail

import com.example.ass07.admin.PetsAdmin
import com.example.ass07.admin.RoomEditType2

import com.example.ass07.customer.Home.Home
import com.example.ass07.customer.Home.Search
import com.example.ass07.customer.LoginRegister.Login
import com.example.ass07.customer.LoginRegister.Register
import com.example.ass07.customer.LoginRegister.ScreenLogin
import com.example.ass07.customer.Mypet.MyPet
import com.example.ass07.customer.Mypet.Mypetedit
import com.example.ass07.customer.Mypet.Mypetinsert
import com.example.ass07.customer.Mypet.PetViewModel
import com.example.ass07.customer.Profile.EditProfile
import com.example.ass07.customer.Profile.Profile
import java.net.URLDecoder

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenAdmin.ManageRoom.route
    ) {
        composable(route = ScreenLogin.Login.route) {
            Login(navController)
        }
        composable(route = ScreenLogin.Register.route) {
            Register(navController)
        }
        ///User
        composable(route = Screen.History.route) {
            History()
        }
        composable(route = Screen.Home.route) {
            Home(navController)
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

        composable(route = Screen.EditProfile.route){
            EditProfile(navController)
        }

        ///Admin
        composable(route = ScreenAdmin.ManageRoom.route) {
            ManageRoom(navController)
        }
        composable(route = ScreenAdmin.Booking.route) {
            com.example.ass07.admin.booking.Booking(navController)
        }
        composable(route = ScreenAdmin.PetsAdmin.route) {
            PetsAdmin(navController)
        }
        composable(route = ScreenAdmin.RoomInsert.route) {
            RoomInsert(navController)
        }

        composable (route =  ScreenAdmin.Dashboard.route){
            AdminDashboard()
        }
        composable(route = ScreenAdmin.RoomEditType.route) {
            RoomEditType(navController)
        }
        composable(route = ScreenAdmin.RoomEdit.route + "/{room_id}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("room_id")?.toIntOrNull()  // รับ room_id จาก URL

            roomId?.let {  // เช็คว่า room_id มีค่าหรือไม่
                RoomEdit(navController, it)  // ส่ง room_id ไปยัง RoomEdit
            }
        }

        composable(
            route = ScreenAdmin.RoomEditType2.route + "/{room_type_id}", // เส้นทาง RoomEditType ที่ต้องมี room_type_id
        ) { backStackEntry ->
            val room_type_id = backStackEntry.arguments?.getString("room_type_id")?.toIntOrNull()
            if (room_type_id != null) {
                RoomEditType2(navController = navController, room_type_id = room_type_id)
            }
        }




        composable(route = ScreenAdmin.BookingDetail.route+"/{id}") { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            BookingDetail(bookingId)
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

            RoomList(roomType = roomType, petType = petType,navController=navController)

        }
        composable("search/{pet}/{checkin}/{checkout}") { backStackEntry ->
            val pet = backStackEntry.arguments?.getString("pet")?.toIntOrNull() ?: 0
            val checkin = backStackEntry.arguments?.getString("checkin") ?: ""
            val checkout = backStackEntry.arguments?.getString("checkout") ?: ""
            Search(navController, pet, checkin, checkout)
        }

//        composable(route = Screen.Search.route) {
//            Search(navController)
//        }

        composable(route = "BookingInfo") {
//            val roomId = 1
//            val days = 2
//            val petType = ""
//            val roomType = ""
//            val checkIn = ""
//            val checkOut = ""
            BookingScreen(navController )
        }
//        composable(
//            route = "payment_screen/{checkIn}/{checkOut}/{totalPrice}",
//        ) { backStackEntry ->
//            val checkIn = backStackEntry.arguments?.getString("checkIn") ?: "N/A"
//            val checkOut = backStackEntry.arguments?.getString("checkOut") ?: "N/A"
//            val totalPrice = backStackEntry.arguments?.getDouble("totalPrice") ?: 0.0 // แปลงเป็น Double
//
//            // ส่งข้อมูลไปยัง PaymentScreen
//            PaymentScreen(navController, checkIn, checkOut, totalPrice)
//        }


//        composable(route = Screen.RoomDetail.route) {
//            HotelBookingScreen(navController)
//        }

        composable(
            route = "RoomDetail",
        ) {

            HotelBookingScreen(
                navController = navController
            )
        }

        composable(
            route = "payment_screen",
        ) { backStackEntry ->
            PaymentScreen(navController)
        }

    }
}