package com.example.ass07.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ass07.customer.Mypet.PetType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomEdit(navController: NavHostController, room_id: Int) {
    var roomTypes by remember { mutableStateOf(listOf<RoomType>()) }
    var selectedRoomType by remember { mutableStateOf<RoomType?>(null) }
    var roomStatus by remember { mutableStateOf(0) }
    var petTypes by remember { mutableStateOf<List<PetType>>(emptyList()) }
    var selectedPetType by remember { mutableStateOf<PetType?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }
    var currentRoom by remember { mutableStateOf<Room?>(null) }

    val createClient = RoomAPI.create()
    val contextForToast = LocalContext.current

    // Fetch the room data to edit
    LaunchedEffect(room_id) {
        createClient.getRoomById(room_id).enqueue(object : Callback<Room> {
            override fun onResponse(call: Call<Room>, response: Response<Room>) {
                if (response.isSuccessful) {
                    currentRoom = response.body()
                    selectedRoomType = roomTypes.find { it.type_id == currentRoom?.room_type_id }
                    selectedPetType = petTypes.find { it.Pet_type_id.toString() == currentRoom?.pet_type }
                    roomStatus = currentRoom?.room_status ?: 0
                }
            }

            override fun onFailure(call: Call<Room>, t: Throwable) {
                Toast.makeText(contextForToast, "ไม่สามารถดึงข้อมูลห้องพักได้", Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch room types
        createClient.getRoomTypes().enqueue(object : Callback<List<RoomType>> {
            override fun onResponse(call: Call<List<RoomType>>, response: Response<List<RoomType>>) {
                if (response.isSuccessful) {
                    roomTypes = response.body() ?: emptyList()
                    if (roomTypes.isNotEmpty()) {
                        selectedRoomType = roomTypes[0]
                    }
                }
            }

            override fun onFailure(call: Call<List<RoomType>>, t: Throwable) {
                Toast.makeText(contextForToast, "โหลดประเภทห้องล้มเหลว", Toast.LENGTH_SHORT).show()
            }
        })

        // Fetch pet types
        createClient.getPetTypes().enqueue(object : Callback<List<PetType>> {
            override fun onResponse(call: Call<List<PetType>>, response: Response<List<PetType>>) {
                if (response.isSuccessful) {
                    petTypes = response.body() ?: emptyList()
                    selectedPetType = petTypes.firstOrNull()
                }
            }

            override fun onFailure(call: Call<List<PetType>>, t: Throwable) {
                Toast.makeText(contextForToast, "โหลดข้อมูลประเภทสัตว์เลี้ยงไม่สำเร็จ", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAF0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigate(ScreenAdmin.ManageRoom.route) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "ย้อนกลับ",
                            tint = Color.Black
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "แก้ไขห้อง",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                RoomTypeDropdown(
                    roomTypes = roomTypes,
                    selectedRoomType = selectedRoomType,
                    petTypes = petTypes,
                    selectedPetType = selectedPetType,
                    onRoomTypeSelected = { selectedRoomType = it },
                    onPetTypeSelected = { selectedPetType = it },
                    onAddNewRoomType = { newTypeName, pricePerDay -> }
                )

                RadioGroupUsage(
                    selected = when (roomStatus) {
                        1 -> "ว่าง"
                        0 -> "ไม่ว่าง"
                        else -> "ซ่อมแซม" // เพิ่มตัวเลือกซ่อมแซม
                    },
                    setSelected = {
                        roomStatus = when (it) {
                            "ว่าง" -> 1
                            "ไม่ว่าง" -> 0
                            "ซ่อมแซม" -> 2
                            else -> roomStatus
                        }
                    },
                    label = "สถานะห้อง",
                    options = listOf("ว่าง", "ไม่ว่าง", "ซ่อมแซม")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedRoomType != null) {
                            val roomTypeId = selectedRoomType?.type_id ?: 0
                            Log.d("API_REQUEST", "roomTypeId: $roomTypeId, roomStatus: $roomStatus")
                            base64Image?.let {
                                createClient.updateroom(
                                    room_id = room_id,
                                    roomTypeId = roomTypeId,
                                    roomStatus = roomStatus,
                                    pet_type = (selectedPetType?.Pet_type_id ?: 0).toString(),
                                    image = it
                                ).enqueue(object : Callback<Room> {
                                    override fun onResponse(call: Call<Room>, response: Response<Room>) {
                                        if (response.isSuccessful) {
                                            Toast.makeText(contextForToast, "แก้ไขห้องสำเร็จ", Toast.LENGTH_SHORT).show()
                                            navController.navigate(ScreenAdmin.ManageRoom.route)
                                        } else {
                                            Toast.makeText(contextForToast, "แก้ไขห้องไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<Room>, t: Throwable) {
                                        Toast.makeText(contextForToast, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_LONG).show()
                                    }
                                })
                            }
                        } else {
                            Toast.makeText(contextForToast, "กรุณาเลือกประเภทห้อง", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("บันทึกการแก้ไข", color = Color.Black)
                }
            }
        }
    }
}