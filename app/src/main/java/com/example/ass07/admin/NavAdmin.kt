package com.example.ass07.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
        composable(route = ScreenAdmin.RoomInsert.route) {
            RoomInsert(navController)
        }
        composable(route = ScreenAdmin.RoomEdit.route + "/{room_id}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("room_id")?.toIntOrNull()  // รับ room_id จาก URL
            val roomViewModel: RoomViewModel = viewModel()  // ใช้ RoomViewModel
            val room by roomViewModel.room.observeAsState()

            LaunchedEffect(roomId) {
                roomId?.let { roomViewModel.loadRoom(it) }  // โหลดข้อมูลห้องตาม roomId
            }

            room?.let { RoomEdit(navController, it.room_id) }  // ส่งข้อมูลห้องไปยัง RoomEdit
        }
    }
}
