package com.example.ass07.admin

import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.benchmark.traceprocessor.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Size
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
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
    var selectedPetTypeName by remember { mutableStateOf("") } // Store the selected pet type name
    var selectedPetTypeId by remember { mutableStateOf(0) } // Store the selected pet type ID
    var petTypes by remember { mutableStateOf<List<PetType>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val api = RoomAPI.create()

    // ดึงข้อมูลประเภทสัตว์เลี้ยง
    LaunchedEffect(Unit) {
        api.getPetTypes().enqueue(object : Callback<List<PetType>> {
            override fun onResponse(call: Call<List<PetType>>, response: Response<List<PetType>>) {
                if (response.isSuccessful) {
                    petTypes = response.body() ?: emptyList()

                    // Find the pet type that matches the room's pet type ID
                    petTypes.find { it.Pet_type_id.toString() == updatedPetType }?.let {
                        selectedPetTypeName = it.Pet_name_type
                        selectedPetTypeId = it.Pet_type_id
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
                    onClick = { navController.navigate(ScreenAdmin.RoomEditType.route)} // Navigate back on click
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
                text = "แก้ไขประเภทห้อง", // ข้อความ
                modifier = Modifier
                    .align(Alignment.Center), // Horizontally and vertically center the text
                fontWeight = FontWeight.Bold, // Bold text
                style = MaterialTheme.typography.titleLarge // Typography style
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(Color(0xFFFFFBEB))
                .size(250.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // เลือกรูปภาพที่จะแสดงตามเงื่อนไข
                val imagePainter = if (imageUri != null) {
                    // กรณีเลือกรูปภาพใหม่
                    rememberAsyncImagePainter(imageUri)
                } else {
                    // กรณีแสดงรูปภาพเดิม
                    when {
                        roomType.image == null || roomType.image.isBlank() -> {
                            Log.d("ImageDebug", "No image available, showing default")
                            painterResource(id = R.drawable.logoapp)  // Default image
                        }
                        roomType.image.startsWith("uploads/") -> {
                            Log.d("ImageDebug", "Loading from relative path: ${roomType.image}")
                            // For development, use a dynamic way to determine the base URL
                            val baseUrl = if (Build.FINGERPRINT.contains("generic")) {
                                // Running on emulator
                                "http://10.0.2.2:3000/"
                            } else {
                                // Running on physical device
                                "http://192.168.1.18:3000/"
                            }
                            rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("$baseUrl${roomType.image}")
                                    .crossfade(true)
                                    .build(),
                                onError = {
                                    Log.e(
                                        "ImageDebug",
                                        "Error loading image: ${roomType.image}, error: ${it.result.throwable.message}"
                                    )
                                }
                            )
                        }
                        roomType.image.startsWith("http") -> {
                            Log.d("ImageDebug", "Loading from full URL: ${roomType.image}")
                            rememberAsyncImagePainter(roomType.image)
                        }
                        else -> {
                            Log.d("ImageDebug", "Unknown image source, showing default")
                            painterResource(id = R.drawable.logoapp)  // Default image in case no match
                        }
                    }
                }

                Image(
                    painter = imagePainter,
                    contentDescription = "Room Type Image",
                    modifier = Modifier
                        .fillMaxSize() // Ensure image fills the entire Box
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }


        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

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

        // Dropdown สำหรับประเภทสัตว์เลี้ยง
        ExposedDropdownMenuBox(
            expanded = petExpanded,
            onExpandedChange = { petExpanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = selectedPetTypeName, // แสดงชื่อประเภทสัตว์เลี้ยงแทนรหัส
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
                            selectedPetTypeName = petType.Pet_name_type // เก็บชื่อที่แสดง
                            selectedPetTypeId = petType.Pet_type_id // เก็บรหัสสำหรับส่งไป API
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
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFBBF24) // amber-400
            ),
            shape = RoundedCornerShape(8.dp)

        ) {
            Text("เลือกรูปภาพ")
        }

        // ปุ่มยืนยันการอัปเดต
        Button(
            onClick = {
                try {
                    val priceValue = updatedPricePerDay.toDoubleOrNull()
                    if (updatedRoomName.isBlank()) {
                        Toast.makeText(context, "กรุณากรอกชื่อประเภทห้อง", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (priceValue == null) {
                        Toast.makeText(context, "กรุณากรอกราคาที่ถูกต้อง", Toast.LENGTH_SHORT)
                            .show()
                        return@Button
                    }
                    if (selectedPetTypeId == 0) {
                        Toast.makeText(context, "กรุณาเลือกประเภทสัตว์เลี้ยง", Toast.LENGTH_SHORT)
                            .show()
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
                        val imagePart =
                            MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

                        // เตรียมข้อมูลฟอร์มอื่นๆ
                        val nameRequestBody =
                            updatedRoomName.toRequestBody("text/plain".toMediaTypeOrNull())
                        val priceRequestBody =
                            updatedPricePerDay.toRequestBody("text/plain".toMediaTypeOrNull())

                        val petTypeRequestBody = selectedPetTypeId.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())

                        // ส่งคำขอสำหรับอัปเดตข้อมูลพร้อมรูปภาพใหม่
                        if (petTypeRequestBody != null) {
                            api.updateRoomTypeWithImage(
                                room_type_id,
                                imagePart,
                                nameRequestBody,
                                priceRequestBody,
                                petTypeRequestBody
                            ).enqueue(object : Callback<RoomTypeResponse> {
                                override fun onResponse(
                                    call: Call<RoomTypeResponse>,
                                    response: Response<RoomTypeResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "อัปเดตสำเร็จ", Toast.LENGTH_SHORT)
                                            .show()
                                        navController.navigate(ScreenAdmin.ManageRoom.route)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "อัปเดตไม่สำเร็จ: ${response.message()}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<RoomTypeResponse>, t: Throwable) {
                                    Toast.makeText(
                                        context,
                                        "เกิดข้อผิดพลาด: ${t.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        }
                    } else {
                        // ไม่มีการเปลี่ยนรูปภาพ ให้อัปเดตเฉพาะข้อมูลอื่นๆ
                        api.updateRoomTypeNoImage(
                            room_type_id = room_type_id,
                            name_type = updatedRoomName,
                            price_per_day = priceValue,
                            pet_type = selectedPetTypeId // เปลี่ยนจาก selectedPet?.Pet_type_id ?? 0 เป็น selectedPetTypeId
                        ).enqueue(object : Callback<RoomTypeResponse> {
                            override fun onResponse(
                                call: Call<RoomTypeResponse>,
                                response: Response<RoomTypeResponse>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "อัปเดตสำเร็จ", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.navigate(ScreenAdmin.ManageRoom.route)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "อัปเดตไม่สำเร็จ: ${response.message()}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<RoomTypeResponse>, t: Throwable) {
                                Toast.makeText(
                                    context,
                                    "เกิดข้อผิดพลาด: ${t.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFBBF24) // amber-400
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("อัปเดตประเภทห้อง")
        }
    }
}