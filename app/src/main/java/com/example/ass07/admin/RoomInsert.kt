package com.example.ass07.admin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import com.example.ass07.customer.Mypet.PetType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomInsert(navController: NavHostController) {
    var roomTypes by remember { mutableStateOf(listOf<RoomType>()) }
    var selectedRoomType by remember { mutableStateOf<RoomType?>(null) }
    var roomStatus by remember { mutableStateOf(0) }
    var isAddingRoomType by remember { mutableStateOf(false) }
    var petTypes by remember { mutableStateOf<List<PetType>>(emptyList()) }
    var selectedPetType by remember { mutableStateOf<PetType?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }

    val createClient = RoomAPI.create()
    val contextForToast = LocalContext.current



    LaunchedEffect(Unit) {
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
                        selectedRoomType = roomTypes[0]
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
                            text = "เพิ่มห้อง",
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
                            image = base64Image
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

                RadioGroupUsage(
                    selected = if (roomStatus == 1) "ว่าง" else "ไม่ว่าง",
                    setSelected = {
                        roomStatus = if (it == "ว่าง") 1 else 0
                    },
                    label = "สถานะห้อง",
                    options = listOf("ว่าง", "ไม่ว่าง")
                )


                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (selectedRoomType != null) {
                            val roomTypeId = selectedRoomType?.type_id ?: 0
                            Log.d("API_REQUEST", "roomTypeId: $roomTypeId, roomStatus: $roomStatus")

                            createClient.insertRoom(
                                roomTypeId = roomTypeId,
                                roomStatus = roomStatus,
                            ).enqueue(object : Callback<Room> {
                                override fun onResponse(call: Call<Room>, response: Response<Room>) {
                                    if (response.isSuccessful) {
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
                    Text("เพิ่มห้อง", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun RadioGroupUsage(
    selected: String,
    setSelected: (String) -> Unit,
    label: String,
    options: List<String>
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "$label: $selected",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selected == item,
                        onClick = { setSelected(item) },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Magenta
                        )
                    )
                    Text(text = item)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomTypeDropdown(
    roomTypes: List<RoomType>,
    selectedRoomType: RoomType?,
    petTypes: List<PetType>,
    selectedPetType: PetType?,
    onRoomTypeSelected: (RoomType) -> Unit,
    onPetTypeSelected: (PetType) -> Unit,
    onAddNewRoomType: (String, Double) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var newRoomTypeName by remember { mutableStateOf("") }
    var newPricePerDay by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var petExpanded by remember { mutableStateOf(false) }
    var selectedPet by remember { mutableStateOf<PetType?>(null) }

    val contextForToast = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            base64Image = encodeImageToBase64(context, it)
            //Log.d("API_REQUEST", "Base64 Image: $base64String")
        }
    }

    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedRoomType?.name_type ?: "เลือกประเภทห้อง",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clickable { expanded = true },
                label = { Text("ประเภทห้อง") },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                roomTypes.forEach { roomType ->
                    DropdownMenuItem(
                        text = { Text(roomType.name_type) },
                        onClick = {
                            onRoomTypeSelected(roomType)
                            expanded = false
                        }
                    )
                }
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("➕ เพิ่มประเภทห้องใหม่") },
                    onClick = {
                        expanded = false
                        showAddDialog = true
                    }
                )
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("เพิ่มประเภทห้องใหม่") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newRoomTypeName,
                            onValueChange = { newRoomTypeName = it },
                            label = { Text("ชื่อประเภทห้อง") }
                        )
                        OutlinedTextField(
                            value = newPricePerDay,
                            onValueChange = { newPricePerDay = it },
                            label = { Text("ราคา/วัน") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )

                        // เลือกประเภทสัตว์เลี้ยง
                        ExposedDropdownMenuBox(
                            expanded = petExpanded,
                            onExpandedChange = { petExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedPet?.Pet_name_type ?: "เลือกประเภทสัตว์เลี้ยง",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .clickable { petExpanded = true },
                                label = { Text("ประเภทสัตว์เลี้ยง") },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown"
                                    )
                                }
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
                                            onPetTypeSelected(petType)
                                            petExpanded = false
                                        }
                                    )
                                }
                            }
                        }


                        // ปุ่มเลือกภาพ
                        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                            Text("เลือกภาพประเภทห้อง")
                        }

                        // แสดงตัวอย่างภาพที่เลือก
                        imageUri?.let {
                            Image(
                                painter = rememberAsyncImagePainter(it),
                                contentDescription = "Room Type Image",
                                modifier = Modifier
                                    .size(150.dp)
                                    .padding(8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newRoomTypeName.isBlank() || newPricePerDay.isBlank() || selectedPet == null) {
                                Toast.makeText(
                                    contextForToast,
                                    "กรุณากรอกข้อมูลให้ครบ",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            val pricePerDay = newPricePerDay.toDoubleOrNull()
                            if (pricePerDay == null) {
                                Toast.makeText(
                                    contextForToast,
                                    "กรุณากรอกราคาให้ถูกต้อง",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            onAddNewRoomType(newRoomTypeName, pricePerDay)
                            showAddDialog = false
                            newRoomTypeName = ""
                            newPricePerDay = ""
                            selectedPet = null
                        }
                    ) {
                        Text("เพิ่ม")
                    }
                },
                dismissButton = {
                    Button(onClick = { showAddDialog = false }) {
                        Text("ยกเลิก")
                    }
                }
            )
        }
    }
}
fun encodeImageToBase64(context: Context, uri: Uri): String? {
    return try {
        val bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // เพิ่ม log เพื่อดู Base64
        Log.d("API_REQUEST", "Base64 Image: $base64String")

        // ตรวจสอบว่า Base64 มีค่าไหม
        if (base64String.isEmpty()) {
            Toast.makeText(context, "Base64 image is empty", Toast.LENGTH_SHORT).show()
        }

        base64String
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}