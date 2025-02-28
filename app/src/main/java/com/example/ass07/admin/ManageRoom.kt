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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ass07.R
import com.example.ass07.admin.Room
import com.example.ass07.admin.RoomAPI
import com.example.ass07.admin.RoomGroupInfo
import com.example.ass07.admin.ScreenAdmin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.ass07.admin.RoomStatus.RoomSort
import com.example.ass07.admin.RoomStatus.RoomFilter

enum class RoomSort {
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    NAME_A_TO_Z,
    NAME_Z_TO_A
}


@Composable
@Preview(showBackground =true)
fun defult(){
    val navController = rememberNavController()
    ManageRoom(navController)
}

@Composable
fun ManageRoom(navController: NavController) {
    val context = LocalContext.current
    var filterDialogOpen by remember { mutableStateOf(false) }
    var sortDialogOpen by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(RoomFilter.ALL) }
    var selectedSort by remember { mutableStateOf<RoomSort?>(null) }
    var rooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var filteredRooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedRoomGroup by remember { mutableStateOf<RoomGroupInfo?>(null) } // เพิ่มการประกาศนี้
    // เรียกข้อมูลจาก API
    LaunchedEffect(Unit) {
        val api = RoomAPI.create()
        api.retrieveAllRooms().enqueue(object : Callback<List<Room>> {
            override fun onResponse(call: Call<List<Room>>, response: Response<List<Room>>) {
                if (response.isSuccessful) {
                    rooms = response.body() ?: emptyList()
                    filteredRooms = rooms
                    Log.d("DEBUG_ROOMS", "Rooms: $rooms") // Debug ค่าห้อง
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

    var selectedRoomType by remember { mutableStateOf<String?>(null) }
    var selectedPetType by remember { mutableStateOf<String?>(null) }

    // ดึงรายการประเภทห้องและสัตว์เลี้ยงที่ไม่ซ้ำกัน
    val uniqueRoomTypes = rooms.map { it.room_type }.distinct()
    val uniquePetTypes = rooms.map { it.pet_type }.distinct()

    // แก้ไขฟังก์ชัน filterAndSortRooms
    fun filterAndSortRooms() {
        var result = rooms

        // กรองตามสถานะ
        result = when (selectedFilter) {
            RoomFilter.AVAILABLE -> result.filter { it.room_status == 1 }
            RoomFilter.OCCUPIED -> result.filter { it.room_status == 0 }
            RoomFilter.ROOM_TYPE -> selectedRoomType?.let { roomType ->
                result.filter { it.room_type == roomType }
            } ?: result
            RoomFilter.PET_TYPE -> selectedPetType?.let { petType ->
                result.filter { it.pet_type == petType }
            } ?: result
            RoomFilter.ALL -> result
        }


        // เรียงลำดับ
        result = when (selectedSort) {
            RoomSort.PRICE_LOW_TO_HIGH -> result.sortedBy { it.price_per_day }
            RoomSort.PRICE_HIGH_TO_LOW -> result.sortedByDescending { it.price_per_day }
            RoomSort.NAME_A_TO_Z -> result.sortedBy { it.room_type }
            RoomSort.NAME_Z_TO_A -> result.sortedByDescending { it.room_type }
            null -> result
        }

        filteredRooms = result
    }

    // อัพเดทรายการห้องเมื่อมีการเปลี่ยนแปลง filter หรือ sort
    LaunchedEffect(selectedFilter, selectedSort, rooms) {
        filterAndSortRooms()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB))
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
                    Text("กำลังโหลดข้อมูล...")
                }
            } else if (errorMessage != null) {
                item {
                    Text("เกิดข้อผิดพลาด: $errorMessage")
                }
            } else {
                // จัดกลุ่มห้องตามประเภทสัตว์เลี้ยง
                val groupedByPetType = filteredRooms.groupBy { it.pet_type }

                groupedByPetType.forEach { (petType, rooms) ->
                    // หัวข้อประเภทสัตว์เลี้ยง
                    item {
                        Text(
                            text = "$petType",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color.Gray
                        )
                    }

                    // จัดกลุ่มห้องที่เหมือนกัน
                    val groupedRooms = rooms.groupBy {
                        Triple(it.room_type, it.price_per_day, it.pet_type)
                    }.map { (key, groupedRooms) ->
                        RoomGroupInfo(
                            roomType = key.first,
                            price = key.second,
                            petType = key.third,
                            availableCount = groupedRooms.count { it.room_status == 1 },
                            occupiedCount = groupedRooms.count { it.room_status == 0 }
                        )
                    }

                    items(groupedRooms) { roomGroup ->
                        GroupedRoomCard(roomGroup = roomGroup) { selectedRoomGroup = roomGroup }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    AddRoomButton(navController)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }


//    Button(
//        onClick = {
//            val roomType = selectedRoomType ?: "All"
//            val petType = selectedPetType ?: "All"
//            navController.navigate("room_list/$roomType/$petType")
//        }
//    ) {
//        Text("ดูรายการห้อง")
//    }
    // แสดงหน้าต่างใหม่เมื่อมีการเลือก RoomGroup
    selectedRoomGroup?.let { roomGroup ->
        navController.navigate("room_list/${roomGroup.roomType}/${roomGroup.petType}")
        selectedRoomGroup = null
    }
    // Filter Dialog
    var showRoomStatusOptions by remember { mutableStateOf(false) }
    var showRoomTypeOptions by remember { mutableStateOf(false) }
    var showPetTypeOptions by remember { mutableStateOf(false) }

    // Filter Dialog
    if (filterDialogOpen) {
        AlertDialog(
            onDismissRequest = { filterDialogOpen = false },
            title = { Text("ตัวกรอง") },
            text = {
                Column {
                    // สถานะห้อง
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showRoomStatusOptions = !showRoomStatusOptions }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "สถานะห้อง",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                painter = painterResource(
                                    id = if (showRoomStatusOptions)
                                        R.drawable.up_arrow else R.drawable.down_arrow
                                ),
                                modifier = Modifier.size(15.dp),
                                contentDescription = "Toggle options"
                            )
                        }

                        if (showRoomStatusOptions) {
                            FilterOption("แสดงทั้งหมด") {
                                selectedFilter = RoomFilter.ALL
                                selectedRoomType = null
                                selectedPetType = null
                                showRoomStatusOptions = false
                            }
                            FilterOption("ห้องว่าง") {
                                selectedFilter = RoomFilter.AVAILABLE
                                selectedRoomType = null
                                selectedPetType = null
                                showRoomStatusOptions = false
                            }
                            FilterOption("ห้องไม่ว่าง") {
                                selectedFilter = RoomFilter.OCCUPIED
                                selectedRoomType = null
                                selectedPetType = null
                                showRoomStatusOptions = false
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // ประเภทห้อง
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showRoomTypeOptions = !showRoomTypeOptions }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "ประเภทห้อง",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                painter = painterResource(
                                    id = if (showRoomTypeOptions)
                                        R.drawable.up_arrow else R.drawable.down_arrow
                                ),modifier = Modifier.size(15.dp),
                                contentDescription = "Toggle options"
                            )
                        }

                        if (showRoomTypeOptions) {
                            uniqueRoomTypes.forEach { roomType ->
                                FilterOption(roomType) {
                                    selectedFilter = RoomFilter.ROOM_TYPE
                                    selectedRoomType = roomType
                                    selectedPetType = null
                                    showRoomTypeOptions = false
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // ประเภทสัตว์เลี้ยง
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showPetTypeOptions = !showPetTypeOptions }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "ประเภทสัตว์เลี้ยง",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                painter = painterResource(
                                    id = if (showPetTypeOptions)
                                        R.drawable.up_arrow else R.drawable.down_arrow
                                ),modifier = Modifier.size(15.dp),
                                contentDescription = "Toggle options"
                            )
                        }

                        if (showPetTypeOptions) {
                            uniquePetTypes.forEach { petType ->
                                FilterOption(petType) {
                                    selectedFilter = RoomFilter.PET_TYPE
                                    selectedPetType = petType
                                    selectedRoomType = null
                                    showPetTypeOptions = false
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { filterDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24))
                ) {
                    Text("ตกลง")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    filterDialogOpen = false
                    selectedFilter = RoomFilter.ALL
                    selectedRoomType = null
                    selectedPetType = null
                    // รีเซ็ตการแสดง dropdown
                    showRoomStatusOptions = false
                    showRoomTypeOptions = false
                    showPetTypeOptions = false
                }) {
                    Text("ยกเลิก")
                }
            }
        )
    }
    // Sort Dialog
    if (sortDialogOpen) {
        AlertDialog(
            onDismissRequest = { sortDialogOpen = false },
            title = { Text("เรียงลำดับ") },
            text = {
                Column {
                    FilterOption("ราคาน้อยไปมาก") {
                        selectedSort = RoomSort.PRICE_LOW_TO_HIGH
                        sortDialogOpen = false
                    }
                    FilterOption("ราคามากไปน้อย") {
                        selectedSort = RoomSort.PRICE_HIGH_TO_LOW
                        sortDialogOpen = false
                    }
                    FilterOption("ชื่อ A-Z") {
                        selectedSort = RoomSort.NAME_A_TO_Z
                        sortDialogOpen = false
                    }
                    FilterOption("ชื่อ Z-A") {
                        selectedSort = RoomSort.NAME_Z_TO_A
                        sortDialogOpen = false
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { sortDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24))
                ) {
                    Text("ตกลง")
                }
            },
            dismissButton = {
                TextButton(onClick = { sortDialogOpen = false }) {
                    Text("ยกเลิก")
                }
            }
        )
    }
}


@Composable
fun GroupedRoomCard(roomGroup: RoomGroupInfo, onCardClick: (RoomGroupInfo) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCardClick(roomGroup) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFFDE68A), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoapp),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roomGroup.roomType,

                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "฿${roomGroup.price}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFD97706)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = "ว่าง ${roomGroup.availableCount} ห้อง",
                        color = Color(0xFF22C55E),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ไม่ว่าง ${roomGroup.occupiedCount} ห้อง",
                        color = Color(0xFFFBBF24),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }



        }
    }

}


@Composable
fun AddRoomButton(navController: NavController) {
    Button(
        onClick = { navController.navigate(ScreenAdmin.RoomInsert.route) },
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
fun RoomCard(room: Room,navController: NavController) {

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
                        color = if (room.room_status == 1) {
                            Color(0xFF22C55E) // สีเขียว (ว่าง)
                        } else {
                            Color(0xFFFBBF24) // สีส้ม (ไม่ว่าง)
                        },
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
                    text = room.pet_type, // ชื่อห้อง
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

            val contextForToast = LocalContext.current
            var expanded by remember { mutableStateOf(false) }

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

                        Icon(Icons.Outlined.Delete, contentDescription = null)
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            textAlign = TextAlign.Start
        )
    }
}
@Composable
fun ShowAllMatchingRooms(
    rooms: List<Room>,
    selectedRoomGroup: RoomGroupInfo,
    navController: NavController
) {
    val filteredRooms = remember(selectedRoomGroup) {
        rooms.filter { room ->
            room.room_type == selectedRoomGroup.roomType && room.pet_type == selectedRoomGroup.petType
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filteredRooms) { room ->
            RoomCard(room = room,navController=navController)

        }
    }
}