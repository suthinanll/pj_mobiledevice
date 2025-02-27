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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.ass07.R
import com.example.ass07.customer.Mypet.PetType
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
    var petExpanded by remember { mutableStateOf(false) }
    var selectedPet by remember { mutableStateOf<PetType?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var petTypes by remember { mutableStateOf<List<PetType>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val api = RoomAPI.create()

    // ดึงข้อมูลประเภทสัตว์เลี้ยง
    LaunchedEffect(Unit) {
        api.getPetTypes().enqueue(object : Callback<List<PetType>> {
            override fun onResponse(call: Call<List<PetType>>, response: Response<List<PetType>>) {
                if (response.isSuccessful) {
                    petTypes = response.body() ?: emptyList()
                    // ตั้งค่าประเภทสัตว์เลี้ยงเริ่มต้นตามข้อมูลที่มีอยู่
                    petTypes.find { it.Pet_name_type == updatedPetType }?.let {
                        selectedPet = it
                    }
                } else {
                    errorMessage = "Error fetching pet types: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<List<PetType>>, t: Throwable) {
                errorMessage = "Error: ${t.message}"
            }
        })
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // ตัวเลือกชื่อประเภทห้อง
        OutlinedTextField(
            value = updatedRoomName,
            onValueChange = { updatedRoomName = it },
            label = { Text("ชื่อประเภทห้อง") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // ตัวเลือกราคาต่อวัน
        OutlinedTextField(
            value = updatedPricePerDay,
            onValueChange = { updatedPricePerDay = it },
            label = { Text("ราคา/วัน") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // ตัวเลือกประเภทสัตว์เลี้ยง
        ExposedDropdownMenuBox(
            expanded = petExpanded,
            onExpandedChange = { petExpanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = updatedPetType ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("ประเภทสัตว์เลี้ยง") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = petExpanded,
                onDismissRequest = { petExpanded = false }
            ) {
                petTypes.forEach { petType ->
                    DropdownMenuItem(
                        text = { Text(petType.Pet_name_type) },
                        onClick = {
                            selectedPet = petType
                            updatedPetType = petType.Pet_name_type
                            petExpanded = false
                        }
                    )
                }
            }
        }

        // เลือกรูปภาพ
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("เลือกรูปภาพ")
        }

        // แสดงตัวอย่างภาพที่เลือก
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Room Type Image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(8.dp)
            )
        } else {
            // แสดงรูปภาพเดิม (ถ้ามี)
            roomType.image?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Current Room Type Image",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(8.dp)
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.logoapp),
                contentDescription = "Default Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(8.dp)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // ปุ่มยืนยันการอัปเดต
        Button(
            onClick = {
                try {
                    val priceValue = updatedPricePerDay.toDoubleOrNull()
                    if (updatedRoomName.isBlank()) {
                        Toast.makeText(context, "กรุณากรอกชื่อประเภทห้อง", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (priceValue == null) {
                        Toast.makeText(context, "กรุณากรอกราคาที่ถูกต้อง", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedPet == null) {
                        Toast.makeText(context, "กรุณาเลือกประเภทสัตว์เลี้ยง", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // ดำเนินการอัปเดตข้อมูล
                    if (imageUri != null) {
                        // มีการเลือกรูปภาพใหม่ ต้องอัปโหลดไฟล์
                        val inputStream = context.contentResolver.openInputStream(imageUri!!)
                            ?: throw Exception("Failed to open input stream")

                        // สร้างไฟล์ชั่วคราวเพื่อบันทึกรูปภาพ
                        val imageFile = File.createTempFile("image", ".jpg", context.cacheDir)
                        val outputStream = FileOutputStream(imageFile)
                        inputStream.copyTo(outputStream)
                        inputStream.close()
                        outputStream.close()

                        // เตรียมรูปภาพสำหรับอัปโหลดเป็น MultipartBody.Part
                        val requestBody = imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

                        // เตรียมข้อมูลฟอร์มอื่นๆ
                        val nameRequestBody = updatedRoomName.toRequestBody("text/plain".toMediaTypeOrNull())
                        val priceRequestBody = updatedPricePerDay.toRequestBody("text/plain".toMediaTypeOrNull())
                        val petTypeRequestBody = selectedPet?.Pet_type_id?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

                        // ส่งคำขอสำหรับอัปเดตข้อมูลพร้อมรูปภาพใหม่
                        if (petTypeRequestBody != null) {
                            api.updateRoomTypeWithImage(
                                room_type_id,
                                imagePart,
                                nameRequestBody,
                                priceRequestBody,
                                petTypeRequestBody
                            ).enqueue(object : Callback<RoomTypeResponse> {
                                override fun onResponse(call: Call<RoomTypeResponse>, response: Response<RoomTypeResponse>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "อัปเดตสำเร็จ", Toast.LENGTH_SHORT).show()
                                        navController.navigate(ScreenAdmin.ManageRoom.route)
                                    } else {
                                        Toast.makeText(context, "อัปเดตไม่สำเร็จ: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<RoomTypeResponse>, t: Throwable) {
                                    Toast.makeText(context, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    } else {
                        // ไม่มีการเปลี่ยนรูปภาพ ให้อัปเดตเฉพาะข้อมูลอื่นๆ
                        api.updateRoomTypeNoImage(
                            room_type_id = room_type_id,
                            name_type = updatedRoomName,
                            price_per_day = priceValue,
                            pet_type = selectedPet?.Pet_type_id ?: 0
                        ).enqueue(object : Callback<RoomTypeResponse> {
                            override fun onResponse(call: Call<RoomTypeResponse>, response: Response<RoomTypeResponse>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "อัปเดตสำเร็จ", Toast.LENGTH_SHORT).show()
                                    navController.navigate(ScreenAdmin.ManageRoom.route)
                                } else {
                                    Toast.makeText(context, "อัปเดตไม่สำเร็จ: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<RoomTypeResponse>, t: Throwable) {
                                Toast.makeText(context, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("อัปเดตประเภทห้อง")
        }
    }
}