package com.example.ass07

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass07.admin.Room
import com.example.ass07.admin.RoomAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun RoomList(
    roomType: String,
    petType: String,
    navController: NavController
) {
    val context = LocalContext.current
    var rooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // โหลดข้อมูลห้องจาก API
    LaunchedEffect(Unit) {
        val api = RoomAPI.create()
        api.retrieveAllRooms().enqueue(object : Callback<List<Room>> {
            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
                if (response.isSuccessful) {
                    rooms = response.body()?.filter {
                        it.room_type == roomType && it.pet_type == petType
                    } ?: emptyList()
                } else {
                    errorMessage = "Error: ${response.message()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<Room>>, t: Throwable) {
                errorMessage = "Error: ${t.message}"
                isLoading = false
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB))
    ) {
        Text(
            text = "ประเภทห้อง: $roomType | ประเภทสัตว์: $petType",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        if (isLoading) {
            Text("กำลังโหลดข้อมูล...", modifier = Modifier.padding(16.dp))
        } else if (errorMessage != null) {
            Text("เกิดข้อผิดพลาด: $errorMessage", modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rooms) { room ->
                    RoomCard(room = room,navController= navController)
                }
            }
        }
    }
}
