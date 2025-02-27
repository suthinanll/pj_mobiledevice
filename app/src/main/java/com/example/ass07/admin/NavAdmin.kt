package com.example.ass07.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ass07.RoomList
import com.example.ass07.admin.booking.BookingDetail
import com.example.ass07.customer.Booking

@Composable
fun NavGraphAdmin(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenAdmin.Dashboard.route
    ) {
        composable(route = ScreenAdmin.ManageRoom.route) {
            ManageRoom(navController)
        }
        composable(route = ScreenAdmin.Booking.route) {
            Booking(navController)
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

//        composable(route = ScreenAdmin.RoomEdit.route + "/{room_id}") { backStackEntry ->
//            val roomId = backStackEntry.arguments?.getString("room_id")?.toIntOrNull()  // รับ room_id จาก URL
//            val roomViewModel: RoomViewModel = viewModel()  // ใช้ RoomViewModel
//            val room by roomViewModel.room.observeAsState()
//

//            roomId?.let {  // เช็คว่า room_id มีค่าหรือไม่
//                RoomEdit(navController, it)  // ส่ง room_id ไปยัง RoomEdit
//
//            }
//        }



//            LaunchedEffect(roomId) {
//                roomId?.let { roomViewModel.loadRoom(it) }  // โหลดข้อมูลห้องตาม roomId
//            }
//
//            room?.let { RoomEdit(navController, it.room_id) }  // ส่งข้อมูลห้องไปยัง RoomEdit
//        }

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

    }
}
