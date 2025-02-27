package com.example.ass07.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.ass07.customer.Mypet.PetType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RoomEditType2(room_type_id: Int, navController: NavController) {
    var roomType by remember { mutableStateOf<RoomType?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch room data from API
    LaunchedEffect(room_type_id) {
        val api = RoomAPI.create()

        // Make GET request to fetch room details by room_type_id
        api.getRoomTypeById(room_type_id).enqueue(object : Callback<RoomType> {
            override fun onResponse(call: Call<RoomType>, response: Response<RoomType>) {
                if (response.isSuccessful) {
                    roomType = response.body()
                } else {
                    errorMessage = "Error: ${response.message()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<RoomType>, t: Throwable) {
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
        if (isLoading) {
            Text("กำลังโหลดข้อมูล...", modifier = Modifier.padding(16.dp))
        } else if (errorMessage != null) {
            Text("เกิดข้อผิดพลาด: $errorMessage", modifier = Modifier.padding(16.dp))
        } else {
            roomType?.let {
                // Room edit form
                RoomEditForm(roomType = it, room_type_id = room_type_id, navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomEditForm(roomType: RoomType, room_type_id: Int, navController: NavController) {
    var updatedRoomName by remember { mutableStateOf(roomType.name_type) }
    var updatedPricePerDay by remember { mutableStateOf(roomType.price_per_day.toString()) }
    var updatedPetType by remember { mutableStateOf(roomType.pet_type) }
    var expanded by remember { mutableStateOf(false) }
    var petExpanded by remember { mutableStateOf(false) }
    var selectedPet by remember { mutableStateOf<PetType?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }
    var petTypes by remember { mutableStateOf<List<PetType>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ดึงข้อมูลประเภทสัตว์เลี้ยง
    LaunchedEffect(Unit) {
        val api = RoomAPI.create()
        api.getPetTypes().enqueue(object : Callback<List<PetType>> {
            override fun onResponse(call: Call<List<PetType>>, response: Response<List<PetType>>) {
                if (response.isSuccessful) {
                    petTypes = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error fetching pet types: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<PetType>>, t: Throwable) {
                errorMessage = "Error: ${t.message}"
            }
        })
    }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {

        }
    }

    Column {
        // ตัวเลือกชื่อประเภทห้อง
        OutlinedTextField(
            value = updatedRoomName,
            onValueChange = { updatedRoomName = it },
            label = { Text("ชื่อประเภทห้อง") }
        )

        // ตัวเลือกราคาต่อวัน
        OutlinedTextField(
            value = updatedPricePerDay,
            onValueChange = { updatedPricePerDay = it },
            label = { Text("ราคา/วัน") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        // ตัวเลือกประเภทสัตว์เลี้ยง
        ExposedDropdownMenuBox(
            expanded = petExpanded,
            onExpandedChange = { petExpanded = it }
        ) {
            updatedPetType?.let {
                OutlinedTextField(
                    value = it,
                    onValueChange = { updatedPetType = it },
                    readOnly = true,
                    label = { Text("ประเภทสัตว์เลี้ยง") }
                )
            }
            ExposedDropdownMenu(
                expanded = petExpanded,
                onDismissRequest = { petExpanded = false }
            ) {
                petTypes.forEach { petType ->
                    DropdownMenuItem(
                        text = { Text(petType.Pet_name_type) },
                        onClick = {
                            selectedPet = petType
                            updatedPetType = petType.Pet_name_type // ใช้ Pet_name_type ที่ถูกต้อง
                            petExpanded = false
                        }
                    )
                }
            }
        }

        // เลือกรูปภาพ
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("เลือกรูปภาพ")
        }

        // แสดงตัวอย่างภาพที่เลือก
        base64Image?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Room Type Image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp)
            )
        }

        // ปุ่มยืนยันการอัปเดต
        Button(
            onClick = {
                if (updatedRoomName.isNotEmpty() && updatedPricePerDay.isNotEmpty()) {
                    val pricePerDay = updatedPricePerDay.toDoubleOrNull()
                    if (pricePerDay != null) {
                        // ส่งคำขอ PUT ไปยังเซิร์ฟเวอร์
                        RoomAPI.create().updateRoomType(
                            room_type_id = room_type_id,
                            name_type = updatedRoomName,
                            price_per_day = pricePerDay,
                            pet_type = selectedPet?.Pet_type_id?.toString() ?: "", // ใช้ Pet_type_id ที่ถูกต้อง
                            image = base64Image // จะเป็น null หากไม่มีการเลือกภาพ
                        ).enqueue(object : Callback<RoomType> {
                            override fun onResponse(call: Call<RoomType>, response: Response<RoomType>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show()
                                    navController.navigate(ScreenAdmin.ManageRoom.route)
                                } else {
                                    Toast.makeText(context, "บันทึกไม่สำเร็จ: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<RoomType>, t: Throwable) {
                                Toast.makeText(context, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                }
            }
        ) {
            Text("อัปเดตประเภทห้อง")
        }
    }
}