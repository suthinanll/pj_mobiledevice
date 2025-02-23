package com.example.ass07.admin

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ass07.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun ManageRoom(navController: NavController) {
    val context = LocalContext.current
    var filterDialogOpen by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var rooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // เรียกข้อมูลจาก API
    LaunchedEffect(Unit) {
        val api = RoomAPI.create()
        api.retrieveAllRooms().enqueue(object : retrofit2.Callback<List<Room>> {
            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
                if (response.isSuccessful) {
                    rooms = response.body() ?: emptyList()
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
            .background(Color(0xFFFFFBEB)) // amber-50 equivalent
    ) {
        // Filter/Sort Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { filterDialogOpen = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6B7280)
                ),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logoapp),
                        contentDescription = "Filter",
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Filter")
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { /* Handle sort */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6B7280)
                ),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logoapp),
                        contentDescription = "Sort",
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Sort")
                }
            }
        }

        // Room List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                item {
                    Text("Loading rooms...")
                }
            } else if (errorMessage != null) {
                item {
                    Text("Error: $errorMessage")
                }
            } else {
                items(rooms) { room ->
                    RoomCard(room = room,navController)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    AddRoomButton(navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Filter Dialog
    if (filterDialogOpen) {
        AlertDialog(
            onDismissRequest = { filterDialogOpen = false },
            title = {
                Text(
                    "Filter Options",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterOption("Available Rooms") {
                        selectedFilter = "available"
                        filterDialogOpen = false
                    }
                    FilterOption("Occupied Rooms") {
                        selectedFilter = "occupied"
                        filterDialogOpen = false
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { filterDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFBBF24) // amber-400
                    )
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { filterDialogOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddRoomButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate(ScreenAdmin.RoomInsert.route)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFBBF24) // amber-400
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add room",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("เพิ่มห้องพัก")
        }
    }
}

@Composable
fun RoomCard(room: Room, navController: NavController) {
    val contextForToast = LocalContext.current
    var expanded by remember { mutableStateOf(false) }



    // ฟังก์ชันลบห้อง (soft delete)
    fun softDeleteRoom(room_id: Int, contextForToast: Context) {
        val createClient = RoomAPI.create()

        createClient.softDeleteRoom(room_id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("SoftDelete", "Response code: ${room_id}") // ✅ Debug Response Code
                if (response.isSuccessful) {
                    Toast.makeText(contextForToast, "ลบข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(contextForToast, "ลบข้อมูลไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                    Log.e("SoftDelete", "Error: ${response.errorBody()?.string()}") // ✅ Debug Error
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(contextForToast, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                Log.e("SoftDelete", "Failure: ${t.message}") // ✅ Debug Failure
            }
        })
    }

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
            // Status indicator (ห้องว่าง/ไม่ว่าง)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (room.room_status == 1) Color(0xFF22C55E) // green-500
                        else Color(0xFFFBBF24), // amber-400
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Room image (ภาพห้อง)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFFDE68A), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                // ใช้รูปภาพห้อง (ถ้ามี)
                Image(
                    painter = painterResource(id = R.drawable.logoapp), // เปลี่ยนเป็นรูปภาพห้องถ้ามี
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Room details (รายละเอียดห้อง)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.room_type, // ชื่อห้อง
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2937) // สีเทาเข้ม
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "฿${room.price_per_day}", // ราคาห้อง
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFD97706) // amber-600
                )
            }

            // เมนู Dropdown
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Open Menu")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // เมนูสำหรับแก้ไขห้อง
                DropdownMenuItem(
                    text = { Text("แก้ไข") },
                    onClick = {
                        Toast.makeText(contextForToast, "แก้ไขห้อง", Toast.LENGTH_SHORT).show()
                        navController.navigate(ScreenAdmin.RoomEdit.route + "/${room.room_id}")
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Settings, contentDescription = null)
                    }
                )

                // เมนูสำหรับลบห้อง
                DropdownMenuItem(
                    text = { Text("ลบ") },
                    onClick = {
                        Toast.makeText(contextForToast, "ลบ", Toast.LENGTH_SHORT).show()
                        expanded = false  // ปิดการแสดงเมนู dropdown
                        softDeleteRoom(room.room_id, contextForToast)  // เรียกใช้ฟังก์ชัน softDeleteRoom
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Settings, contentDescription = null)
                    }
                )
            }
        }
    }
}

@Composable
fun FilterOption(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color(0xFF6B7280)
        )
    ) {
        Text(text)
    }
}


