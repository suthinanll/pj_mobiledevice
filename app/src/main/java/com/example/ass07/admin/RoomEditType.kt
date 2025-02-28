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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.ass07.admin.Room
import com.example.ass07.admin.RoomAPI
import com.example.ass07.admin.RoomType
import com.example.ass07.admin.ScreenAdmin
import com.example.ass07.customer.Mypet.PetType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RoomEditType(navController: NavController) {
    val contextForToast = LocalContext.current
    var rooms by remember { mutableStateOf<List<RoomType>>(emptyList()) }
    var petTypes by remember { mutableStateOf<List<PetType>>(emptyList()) }  // ดึงข้อมูล pet_type
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch room data from API
    LaunchedEffect(Unit) {
        val api = RoomAPI.create()

        // ดึงข้อมูล RoomTypes (ห้อง)
        api.getRoomTypes().enqueue(object : Callback<List<RoomType>> {
            override fun onResponse(call: Call<List<RoomType>>, response: Response<List<RoomType>>) {
                if (response.isSuccessful) {
                    rooms = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error: ${response.message()}"
                    Log.d("RoomEditType", "Error fetching room: ${response.message()}")
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<RoomType>>, t: Throwable) {
                errorMessage = "Error: ${t.message}"
                isLoading = false
            }
        })

        // ดึงข้อมูล petTypes (ประเภทสัตว์เลี้ยง)
        api.getPetTypes().enqueue(object : Callback<List<PetType>> {
            override fun onResponse(call: Call<List<PetType>>, response: Response<List<PetType>>) {
                if (response.isSuccessful) {
                    petTypes = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error: ${response.message()}"
                    Log.d("RoomEditType", "Error fetching pet types: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<PetType>>, t: Throwable) {
                errorMessage = "Error: ${t.message}"
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
            Spacer(modifier = Modifier.height(5.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width of the screen
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Fill the width to space elements
                        .align(Alignment.CenterStart), // Aligning the content to the left (back button)
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigate(ScreenAdmin.ManageRoom.route)} // Navigate back on click
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Centered Text
                Text(
                    text = "ประเภทห้องทั้งหมด", // ข้อความ
                    modifier = Modifier
                        .align(Alignment.Center), // Horizontally and vertically center the text
                    fontWeight = FontWeight.Bold, // Bold text
                    style = MaterialTheme.typography.titleLarge // Typography style
                )
            }

            // Use LazyColumn to display all rooms
            Spacer(modifier = Modifier.width(25.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rooms) { room ->
                    RoomCardEdit(roomtype = room, petTypes = petTypes, navController = navController)
                }
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
    }
}

@Composable
fun RoomCardEdit(roomtype: RoomType, petTypes: List<PetType>, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    val contextForToast = LocalContext.current


    // หาค่าชื่อสัตว์เลี้ยงที่ตรงกับ pet_type_id
    val petName = petTypes.find { it.Pet_type_id.toString() == roomtype.pet_type }?.Pet_name_type ?: "ไม่ระบุ"


    fun softDeleteRoomType(room_type_id: Int, contextForToast: Context) {
        val createClient = RoomAPI.create()
        createClient.softDeleteRoomType(room_type_id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("SoftDelete", "Response code: ${room_type_id}") // ✅ Debug Response Code
                if (response.isSuccessful) {
                    Toast.makeText(contextForToast, "ลบประเภทห้องสำเร็จ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(contextForToast, "ลบประเภทห้องไม่สำเร็จ", Toast.LENGTH_SHORT).show()
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
            Spacer(modifier = Modifier.width(16.dp))

            // Room image (ภาพห้อง)
            Box(
                modifier = Modifier
                    .size(64.dp) // ขนาดของกล่องที่มีขอบสีเหลือง
                    .background(Color(0xFFFDE68A), RoundedCornerShape(8.dp)) // ขอบสีเหลือง
                    .padding(5.dp)
            ) {
                // Display room image (use a placeholder if no image)
                val imageUri = roomtype.image ?: "" // Check if image is null
                Image(
                    painter = if (imageUri.isEmpty()) {
                        painterResource(id = R.drawable.logoapp) // Use the default logo
                    } else {
                        val imagePath =
                            roomtype.image?.replace("uploads/", "")  // Remove "uploads/" from the path
                        rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("http://10.0.2.2:3000/${roomtype.image}")
                                .crossfade(true)
                                .build(),
                            onError = {
                                Log.e("ImageDebug", "Error loading image: ${roomtype.image}, error: ${it.result.throwable.message}")
                            }
                        )
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize() // ใช้ fillMaxSize เพื่อให้ภาพขยายเต็มขนาดของ Box
                        .clip(RoundedCornerShape(5.dp)), // ใช้ fillMaxSize เพื่อให้ภาพขยายเต็มขนาดของ Box
                    contentScale = ContentScale.Crop // ใช้ fillMaxSize เพื่อให้ภาพขยายเต็มขนาดของ Box
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
                    text = petName, // Pet type name from the API
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
                        navController.navigate(ScreenAdmin.RoomEditType2.route + "/${roomtype.room_type_id}")
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
                        softDeleteRoomType(roomtype.room_type_id, contextForToast)
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Delete, contentDescription = null)
                    }
                )
            }
        }
    }
}