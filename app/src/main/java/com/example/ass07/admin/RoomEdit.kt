package com.example.ass07.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.ass07.customer.Mypet.PetType
import com.example.ass07.customer.Mypet.PetTypeDropdown
import com.example.ass07.customer.Mypet.petMember
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun RoomEdit(navController: NavHostController, room_id: Int) {
    var room by remember { mutableStateOf<Room?>(null) }
    var loading by remember { mutableStateOf(true) }
    var roomTypes by remember { mutableStateOf(listOf<RoomType>()) }
    var selectedRoomType by remember { mutableStateOf<RoomType?>(null) }
    var roomStatus by remember { mutableStateOf(0) }
    var petTypes by remember { mutableStateOf<List<PetType>>(emptyList()) }
    var selectedPetType by remember { mutableStateOf<PetType?>(null) }
    var isAddingRoomType by remember { mutableStateOf(false) }
    var base64Image by remember { mutableStateOf<String?>(null) }

    val createClient = RoomAPI.create()
    val contextForToast = LocalContext.current

    // เรียกข้อมูลห้องและข้อมูลประเภทสัตว์เลี้ยง
    LaunchedEffect(room_id) {
        createClient.getRoomById(room_id).enqueue(object : Callback<Room> {
            override fun onResponse(call: Call<Room>, response: Response<Room>) {
                if (response.isSuccessful) {
                    room = response.body()

                    room?.let {
                        // ตั้งค่า roomStatus ให้ตรงกับสถานะห้องที่ได้รับจาก API
                        roomStatus = it.room_status // กำหนดสถานะห้องที่ดึงมาจาก API
                        Log.e("API_ERROR", "Room Status: ${roomStatus}") // แสดงค่า roomStatus
                    }
                    loading = false
                }
            }

            override fun onFailure(call: Call<Room>, t: Throwable) {
                loading = false
                Log.e("API_ERROR", "Failed to fetch room data: ${t.message}")
            }
        })

        createClient.getPetTypes().enqueue(object : Callback<List<PetType>> {
            override fun onResponse(call: Call<List<PetType>>, response: Response<List<PetType>>) {
                if (response.isSuccessful) {
                    petTypes = response.body() ?: emptyList()
                }
            }

            override fun onFailure(call: Call<List<PetType>>, t: Throwable) {
                Toast.makeText(
                    contextForToast,
                    "โหลดข้อมูลประเภทสัตว์เลี้ยงไม่สำเร็จ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        createClient.getRoomTypes().enqueue(object : Callback<List<RoomType>> {
            override fun onResponse(call: Call<List<RoomType>>, response: Response<List<RoomType>>) {
                if (response.isSuccessful) {
                    roomTypes = response.body() ?: emptyList()
                    if (roomTypes.isNotEmpty()) {
                        // ค้นหา index ของ roomType ที่ตรงกับ room_type_id
                        room?.let {
                            val index = roomTypes.indexOfFirst { roomType -> roomType.room_type_id == it.type_type_id }
                            if (index != -1) {
                                selectedRoomType = roomTypes[index]
                                Log.e("API_ERROR", "selectedRoomType: ${selectedRoomType?.name_type}")
                            } else {
                                Log.e("API_ERROR", "Room type not found")
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<RoomType>>, t: Throwable) {
                Toast.makeText(
                    contextForToast,
                    "โหลดประเภทห้องล้มเหลว",
                    Toast.LENGTH_SHORT
                ).show()
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
                    onAddNewRoomType = { newTypeName, pricePerDay ->
                        if (selectedPetType == null) {
                            Toast.makeText(
                                contextForToast,
                                "กรุณาเลือกประเภทสัตว์เลี้ยง",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@RoomTypeDropdown
                        }
                        isAddingRoomType = true
                        createClient.addRoomType(
                            name_type = newTypeName,
                            price_per_day = pricePerDay,
                            pet_type = selectedPetType!!.Pet_type_id.toString() ,
                            image = base64Image // ส่ง Base64 ไปที่ API
                        ).enqueue(object : Callback<RoomTypeResponse> {
                            override fun onResponse(
                                call: Call<RoomTypeResponse>,
                                response: Response<RoomTypeResponse>
                            ) {
                                isAddingRoomType = false
                                if (response.isSuccessful) {
                                    val newRoomType = response.body()?.roomType
                                    if (newRoomType != null) {
                                        if (roomTypes.none { it.name_type == newRoomType.name_type }) {
                                            roomTypes = roomTypes.toMutableList().apply { add(newRoomType) }
                                            selectedRoomType = newRoomType
                                        }
                                        Toast.makeText(
                                            contextForToast,
                                            "เพิ่มประเภทห้องพักสำเร็จ",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            contextForToast,
                                            "ไม่สามารถเพิ่มประเภทห้องพัก",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        contextForToast,
                                        "ไม่สามารถเพิ่มประเภทห้องพัก",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<RoomTypeResponse>, t: Throwable) {
                                isAddingRoomType = false
                                Toast.makeText(
                                    contextForToast,
                                    "เกิดข้อผิดพลาด: ${t.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                )


                // เลือกสถานะห้อง
                RadioGroupUsage(
                    selected = when (roomStatus) {
                        1 -> "ว่าง"
                        0 -> "ไม่ว่าง"
                        3 -> "ปรับปรุง"
                        else -> ""
                    },
                    setSelected = {
                        roomStatus = when (it) {
                            "ว่าง" -> 1
                            "ไม่ว่าง" -> 0
                            "ปรับปรุง" -> 3
                            else -> roomStatus
                        }
                    },
                    label = "สถานะห้อง",
                    options = listOf("ว่าง", "ไม่ว่าง", "ปรับปรุง")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedRoomType != null) {
                            val roomTypeId = selectedRoomType?.room_type_id ?: 0
                            createClient.updateroom(
                                room_id = room_id,
                                roomTypeId = roomTypeId,
                                roomStatus = roomStatus
                            ).enqueue(object : Callback<Room> {
                                override fun onResponse(call: Call<Room>, response: Response<Room>) {
                                    if (response.isSuccessful) {
                                        Log.d("RoomEdit", "RoomType ID: ${selectedRoomType?.room_type_id}")
                                        Log.d("RoomEdit", "RoomStatus: $roomStatus")
                                        Log.d("RoomEdit", "room_id: $room_id")
                                        Toast.makeText(contextForToast, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show()
                                        navController.navigate(ScreenAdmin.ManageRoom.route)
                                    } else {
                                        Toast.makeText(contextForToast, "บันทึกไม่สำเร็จ: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<Room>, t: Throwable) {
                                    Toast.makeText(contextForToast, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_LONG).show()
                                }
                            })
                        } else {
                            Toast.makeText(contextForToast, "กรุณาเลือกประเภทห้อง", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("update", color = Color.Black)
                }
            }
        }
    }
}


