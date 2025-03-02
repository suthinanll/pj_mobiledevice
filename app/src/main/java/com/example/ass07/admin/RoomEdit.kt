package com.example.ass07.admin

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ass07.customer.Mypet.petMember
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


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
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var currentRoom by remember { mutableStateOf<Room?>(null) }
    Log.e("RoomEdit", "Received room_id: $room_id")

    val createClient = RoomAPI.create()
    val contextForToast = LocalContext.current
    Toast.makeText(contextForToast, "แก้ไข", Toast.LENGTH_SHORT).show()

    // เรียกข้อมูลห้องและข้อมูลประเภทสัตว์เลี้ยง
    LaunchedEffect(room_id) {
        createClient.getRoomById(room_id).enqueue(object : Callback<Room> {
            override fun onResponse(call: Call<Room>, response: Response<Room>) {
                if (response.isSuccessful) {
                    room = response.body()

                    Log.e("Error",room.toString())
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
            override fun onResponse(
                call: Call<List<RoomType>>,
                response: Response<List<RoomType>>
            ) {
                if (response.isSuccessful) {
                    roomTypes = response.body() ?: emptyList()
                    if (roomTypes.isNotEmpty()) {
                        // ค้นหา index ของ roomType ที่ตรงกับ room_type_id
                        room?.let {
                            val index =
                                roomTypes.indexOfFirst { roomType -> roomType.room_type_id == it.type_type_id }
                            if (index != -1) {
                                selectedRoomType = roomTypes[index]
                                Log.e(
                                    "API_ERROR",
                                    "selectedRoomType: ${selectedRoomType?.name_type}"
                                )
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
            .background(Color(0xFFFFFBEB)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Fill the width of the screen
                    .padding(16.dp)
                    .background(Color(0xFFFFFBEB))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth() // Fill the width to space elements
                        .align(Alignment.CenterStart), // Aligning the content to the left (back button)
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigate(ScreenAdmin.ManageRoom.route) } // Navigate back on click
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
                    text = "แก้ไขห้อง", // ข้อความ
                    modifier = Modifier
                        .align(Alignment.Center), // Horizontally and vertically center the text
                    fontWeight = FontWeight.Bold, // Bold text
                    style = MaterialTheme.typography.titleLarge // Typography style
                )
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
                    val imageFile = imageUri?.let { uri ->
                        val inputStream = contextForToast.contentResolver.openInputStream(uri)
                        val imageFile =
                            File.createTempFile("image", ".jpg", contextForToast.cacheDir)
                        val outputStream = FileOutputStream(imageFile)
                        inputStream?.copyTo(outputStream)
                        inputStream?.close()
                        outputStream.close()
                        imageFile
                    }

                    val requestBody = imageFile?.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imagePart: MultipartBody.Part? = requestBody?.let {
                        MultipartBody.Part.createFormData("image", imageFile.name, it)
                    }
                    val nameRequestBody = newTypeName.toRequestBody("text/plain".toMediaTypeOrNull())
                    val priceRequestBody = pricePerDay.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val petTypeRequestBody = selectedPetType!!.Pet_type_id.toString().toRequestBody("text/plain".toMediaTypeOrNull())


                    if (imagePart != null) {
                        createClient.addRoomType(
                            name_type = nameRequestBody,
                            price_per_day = priceRequestBody,
                            pet_type = petTypeRequestBody,
                            image = imagePart
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
                                            roomTypes =
                                                roomTypes.toMutableList().apply { add(newRoomType) }
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
                                    Log.d(
                                        "RoomEdit",
                                        "RoomType ID: ${selectedRoomType?.room_type_id}"
                                    )
                                    Log.d("RoomEdit", "RoomStatus: $roomStatus")
                                    Log.d("RoomEdit", "room_id: $room_id")
                                    Toast.makeText(
                                        contextForToast,
                                        "บันทึกสำเร็จ",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(ScreenAdmin.ManageRoom.route)
                                } else {
                                    Toast.makeText(
                                        contextForToast,
                                        "บันทึกไม่สำเร็จ: ${response.message()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<Room>, t: Throwable) {
                                Toast.makeText(
                                    contextForToast,
                                    "เกิดข้อผิดพลาด: ${t.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    } else {
                        Toast.makeText(contextForToast, "กรุณาเลือกประเภทห้อง", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFBBF24) // amber-400
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("แก้ไข", color = Color.Black)
            }
        }
    }

}


