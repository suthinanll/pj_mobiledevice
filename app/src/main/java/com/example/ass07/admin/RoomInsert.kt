package com.example.ass07.admin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomInsert(navController: NavHostController) {
    var roomTypes by remember { mutableStateOf(listOf<RoomType>()) }
    var selectedRoomType by remember { mutableStateOf<RoomType?>(null) }
    var roomStatus by remember { mutableStateOf(0) }
    var isAddingRoomType by remember { mutableStateOf(false) }
    var petTypes by remember { mutableStateOf<List<PetType>>(emptyList()) }
    var selectedPetType by remember { mutableStateOf<PetType?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

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
            override fun onResponse(
                call: Call<List<RoomType>>,
                response: Response<List<RoomType>>
            ) {
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
                    text = "เพิ่มห้อง", // ข้อความ
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

                    // Upload image along with room type info
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

                    // Upload image along with room type info
                    val requestBody = imageFile?.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imagePart: MultipartBody.Part? = requestBody?.let {
                        MultipartBody.Part.createFormData("image", imageFile.name, it)
                    }
                    val nameRequestBody = newTypeName.toRequestBody("text/plain".toMediaTypeOrNull())
                    val priceRequestBody = pricePerDay.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val petTypeRequestBody = selectedPetType!!.Pet_type_id.toString().toRequestBody("text/plain".toMediaTypeOrNull())

// เรียกใช้ API ด้วยพารามิเตอร์ที่ถูกต้อง
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
                                    Log.d("API_REQUEST", "newRoomType: $newRoomType")
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
                                        navController.navigate(ScreenAdmin.ManageRoom.route)
                                    } else {
                                        Toast.makeText(
                                            contextForToast,
                                            "ลองรีอีกรอบ",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                } else {
                                    Toast.makeText(
                                        contextForToast,
                                        "ไม่สามารเพิ่มประเภทห้องพักได้",
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


            RadioGroupUsage(
                selected = if (roomStatus == 1) "ว่าง" else "ไม่ว่าง",
                setSelected = {
                    roomStatus = if (it == "ว่าง") 1 else 0
                },
                label = "สถานะห้อง",
                options = listOf("ว่าง", "ไม่ว่าง")
            )
            val context = LocalContext.current
            val contentResolver = context.contentResolver


            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedRoomType != null) {
                        val roomTypeId = selectedRoomType!!.room_type_id // แก้ไขตรงนี้ ไม่ใช้ ?: 0
                        Log.d("API_REQUEST", "roomTypeId: $roomTypeId, roomStatus: $roomStatus")


                        imageUri?.let { uri ->  // Use the imageUri state here
                            val bitmap =
                                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                            val imageFile = imageUri?.let { uri ->
                                val bitmap =
                                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                                val file = File.createTempFile("image", ".jpg")
                                val outputStream = FileOutputStream(file)
                                bitmap.compress(
                                    Bitmap.CompressFormat.JPEG,
                                    80,
                                    outputStream
                                ) // ลดขนาดภาพ
                                outputStream.flush()
                                outputStream.close()
                                file
                            }
                            val requestBody =
                                imageFile?.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            val imagePart = requestBody?.let {
                                MultipartBody.Part.createFormData("image", imageFile.name, it)
                            }

                        }



                        createClient.insertRoom(
                            roomTypeId = roomTypeId,
                            roomStatus = roomStatus,
                        ).enqueue(object : Callback<Room> {
                            override fun onResponse(call: Call<Room>, response: Response<Room>) {
                                if (response.isSuccessful) {
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
                Text("เพิ่มห้อง", color = Color.Black)
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
    onAddNewRoomType: (String, Double) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var newRoomTypeName by remember { mutableStateOf("") }
    var newPricePerDay by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var petExpanded by remember { mutableStateOf(false) }
    var selectedPet by remember { mutableStateOf<PetType?>(null) }
    val createClient = RoomAPI.create()

    val contextForToast = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
            }
        }
    )

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
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = newRoomTypeName,
                            onValueChange = { newRoomTypeName = it },
                            label = { Text("ชื่อประเภทห้อง") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newPricePerDay,
                            onValueChange = { newPricePerDay = it },
                            label = { Text("ราคา/วัน") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

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

                        Spacer(modifier = Modifier.height(16.dp))

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
                            Image(
                                painter = painterResource(id = R.drawable.logoapp),
                                contentDescription = "Default Logo",
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // ปุ่มเลือกรูปภาพ
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("เลือกภาพประเภทห้อง")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            try {
                                val priceValue = newPricePerDay.toDoubleOrNull()
                                if (newRoomTypeName.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "กรุณากรอกชื่อประเภทห้อง",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                if (priceValue == null) {
                                    Toast.makeText(
                                        context,
                                        "กรุณากรอกราคาที่ถูกต้อง",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                if (selectedPet == null) {
                                    Toast.makeText(
                                        context,
                                        "กรุณาเลือกประเภทสัตว์เลี้ยง",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                if (imageUri == null) {
                                    Toast.makeText(context, "กรุณาเลือกรูปภาพ", Toast.LENGTH_SHORT)
                                        .show()
                                    return@Button
                                    Log.d("ImagePicker", "Selected Image URI: $imageUri")

                                }

                                val inputStream = imageUri?.let {
                                    contextForToast.contentResolver.openInputStream(it)
                                } ?: throw Exception("Failed to open input stream")

                                // Create a temporary file to save the image
                                val imageFile = File.createTempFile("image", ".jpg")

                                // Create an output stream to save the image file
                                val outputStream = FileOutputStream(imageFile)
                                inputStream.copyTo(outputStream)

                                // Close the streams
                                inputStream.close()
                                outputStream.close()

                                // Prepare the image for uploading as MultipartBody.Part
                                val requestBody =
                                    imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                val imagePart = MultipartBody.Part.createFormData(
                                    "image",
                                    imageFile.name,
                                    requestBody
                                )

                                // Prepare other form data: Room name, Price per day, and Pet type
                                val titleRequestBody =
                                    newRoomTypeName.toRequestBody("text/plain".toMediaTypeOrNull())
                                val priceRequestBody =
                                    newPricePerDay.toRequestBody("text/plain".toMediaTypeOrNull())
                                val petTypeRequestBody = selectedPet?.Pet_type_id?.toString()
                                    ?.toRequestBody("text/plain".toMediaTypeOrNull())

                                // Perform the network request to upload the data
                                if (petTypeRequestBody != null) {
                                    createClient.uploadRoomData(
                                        imagePart,
                                        titleRequestBody,
                                        priceRequestBody,
                                        petTypeRequestBody
                                    )
                                        .enqueue(object : Callback<RoomType> {
                                            override fun onResponse(
                                                call: Call<RoomType>,
                                                response: Response<RoomType>
                                            ) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(
                                                        context,
                                                        "บันทึกสำเร็จ",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    onAddNewRoomType(newRoomTypeName, priceValue)
                                                    showAddDialog = false
                                                    newRoomTypeName = ""
                                                    newPricePerDay = ""
                                                    selectedPet = null
                                                    imageUri = null

                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        "บันทึกไม่สำเร็จ: ${response.message()}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<RoomType>,
                                                t: Throwable
                                            ) {
                                                Toast.makeText(
                                                    context,
                                                    "เกิดข้อผิดพลาด: ${t.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        })
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    contextForToast,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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