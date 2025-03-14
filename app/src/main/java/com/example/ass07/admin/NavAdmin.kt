package com.example.ass07.admin

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ass07.RoomEditType
import com.example.ass07.RoomList
import com.example.ass07.admin.booking.BookingDetail

@Composable
fun NavGraphAdmin(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ScreenAdmin.Dashboard.route
    ) {
        composable(route = ScreenAdmin.ManageRoom.route ) {
            ManageRoom(navController)
        }
        composable(route = ScreenAdmin.PetsAdmin.route) {
            PetsAdmin(navController)
        }
        composable(route = ScreenAdmin.RoomInsert.route) {
            RoomInsert(navController)
        }

        composable (route =  ScreenAdmin.Dashboard.route){
            AdminDashboard(navController = navController)
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

    }
}
