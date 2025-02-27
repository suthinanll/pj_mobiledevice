package com.example.ass07

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.ass07.admin.Room
import com.example.ass07.admin.RoomAPI
import com.example.ass07.admin.RoomType
import com.example.ass07.admin.ScreenAdmin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RoomEditType(navController: NavController, room_type_id: Int) {
    val contextForToast = LocalContext.current
    var rooms by remember { mutableStateOf<List<RoomType>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch room data based on room_type_id from API
    LaunchedEffect(room_type_id) {
        val api = RoomAPI.create()
        api.getRoomTypes().enqueue(object : Callback<List<RoomType>> {
            override fun onResponse(call: Call<List<RoomType>>, response: Response<List<RoomType>>) {
                if (response.isSuccessful) {
                    rooms = response.body()?.filter { it.room_type_id == room_type_id } ?: emptyList()
                    Log.d("RoomEditType", "Room data fetched: $rooms")
                } else {
                    errorMessage = "Error: ${response.message()}"
                    Log.d("RoomEditType", "Error fetching room: ${response.message()}")
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<RoomType>>, t: Throwable) {
                errorMessage = "Error: ${t.message}"
                Log.d("RoomEditType", "Failure fetching room: ${t.message}")
                isLoading = false
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB))
    ) {
        if (isLoading) {
            Text("กำลังโหลดข้อมูล...", modifier = Modifier.padding(16.dp))
        } else if (errorMessage != null) {
            Text("เกิดข้อผิดพลาด: $errorMessage", modifier = Modifier.padding(16.dp))
        } else {
            // Use LazyColumn to loop through the room data
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rooms) { room ->
                    RoomCardEdit(roomtype = room, navController = navController)
                }
            }
        }
    }
}

@Composable
fun RoomCardEdit(roomtype: RoomType, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { /* Handle click to view details or edit */ },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))

            // Room image (ภาพห้อง)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFFDE68A), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // Display room image (use a placeholder if no image)
                Image(
                    //painter = rememberImagePainter(room.image),
                    painter = painterResource(id = R.drawable.logoapp),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Room details (room type, pet type, and price per day)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roomtype.name_type, // Room name
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2937) // Dark gray
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = roomtype.pet_type_name, // Pet type name (instead of pet_type)
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2937) // Dark gray
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "฿${roomtype.price_per_day}", // Price per day
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFD97706) // amber-600
                )
            }

            // Dropdown menu for more options
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Open Menu")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color.White)
                    .border(1.dp, Color(0xFFFFD966)) // Yellow border
                    .padding(8.dp) // Adjust padding as needed
            ) {
                // Menu for editing room
                DropdownMenuItem(
                    text = { Text("แก้ไข", color = Color.Black) },
                    onClick = {
                        navController.navigate(ScreenAdmin.RoomEdit.route + "/${roomtype.room_type_id}")
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Settings, contentDescription = null)
                    }
                )

                // Menu for deleting room (soft delete)
                DropdownMenuItem(
                    text = { Text("ลบ", color = Color.Red) },
                    onClick = {
                        expanded = false
                        // Call soft delete function if needed
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Delete, contentDescription = null)
                    }
                )
            }
        }
    }
}