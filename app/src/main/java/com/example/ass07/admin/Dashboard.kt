package com.example.ass07.admin

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import com.example.ass07.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.ass07.admin.booking.BookingAPI
import com.example.ass07.admin.booking.Booking
import com.example.ass07.admin.PetApi
import com.example.ass07.customer.LoginRegister.LoginClass
import com.example.ass07.customer.LoginRegister.ScreenLogin
import com.example.ass07.customer.LoginRegister.SharePreferencesManager
import com.example.ass07.customer.Screen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminDashboard(
    onNavigateToBookingDetails: (Int) -> Unit = {},
    onNavigateToRooms: () -> Unit = {},
    onNavigateToPets: () -> Unit = {},
    navController : NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val roomAPI = remember { RoomAPI.create() }
    val bookingAPI = remember { BookingAPI.create() }
    val petAPI = remember { PetApi.create() } // เพิ่ม PetAPI

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Room data
    var rooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var totalPets by remember { mutableStateOf(0) }
    var availableRooms by remember { mutableStateOf(0) }
    var roomStatistics by remember { mutableStateOf<List<RoomStatistic>>(emptyList()) }

    // Booking data
    var bookings by remember { mutableStateOf<List<Booking>>(emptyList()) }
    var activeBookings by remember { mutableStateOf(0) }
    var pendingCheckouts by remember { mutableStateOf(0) }
    var todayCheckIns by remember { mutableStateOf(0) }
    var recentBookings by remember { mutableStateOf<List<Booking>>(emptyList()) }

    // Selected tab
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                // เพิ่มการโหลดข้อมูลสัตว์เลี้ยงทั้งหมด
                val petResponse = petAPI.retrievepetMember().execute()

                // Load rooms data
                val roomResponse = roomAPI.retrieveAllRooms().execute()

                // Load bookings data
                val bookingResponse = bookingAPI.getBookings().execute()

                withContext(Dispatchers.Main) {
                    if (roomResponse.isSuccessful && bookingResponse.isSuccessful && petResponse.isSuccessful) {
                        // Process pet data - ดึงข้อมูลสัตว์เลี้ยงทั้งหมด
                        val petsList = petResponse.body()
                        if (petsList != null) {
                            totalPets = petsList.size // นับจำนวนสัตว์เลี้ยงทั้งหมด
                        }

                        // Process room data
                        val roomsList = roomResponse.body()
                        if (roomsList != null) {
                            rooms = roomsList
                            availableRooms = rooms.count { it.room_status == 0 }

                            roomStatistics = rooms
                                .groupBy { it.pet_type }
                                .map { (petType, roomsForType) ->
                                    RoomStatistic(
                                        type = petType,
                                        available = roomsForType.count { it.room_status == 0 },
                                        booked = roomsForType.count { it.room_status == 1 },
                                        clean = roomsForType.count { it.room_status == 2 }
                                    )
                                }
                        }

                        // Process booking data
                        val bookingsList = bookingResponse.body()
                        if (bookingsList != null) {
                            bookings = bookingsList

                            // ไม่ต้องคำนวณ totalPets จาก bookings อีกต่อไป เพราะเราดึงจาก petAPI แล้ว
                            // totalPets = bookings.map { it.petId }.distinct().size

                            // ส่วนที่เหลือของโค้ดยังคงเหมือนเดิม
                            val today = LocalDate.now()
                            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                            activeBookings = bookings.count { it.status == 1 }

                            pendingCheckouts = bookings.count { booking ->
                                try {
                                    val checkoutDate = LocalDate.parse(booking.checkOut.split("T")[0], dateFormatter)
                                    val daysUntilCheckout = ChronoUnit.DAYS.between(today, checkoutDate)
                                    booking.status == 1 && daysUntilCheckout in 0..2
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            todayCheckIns = bookings.count { booking ->
                                try {
                                    val checkinDate = LocalDate.parse(booking.checkIn.split("T")[0], dateFormatter)
                                    booking.status == 0 && today.isEqual(checkinDate)
                                } catch (e: Exception) {
                                    false
                                }
                            }

                            recentBookings = bookings
                                .sortedByDescending { it.createdAt }
                                .take(5)
                        }
                    } else {
                        // ปรับ error handling เพื่อรวม pet API
                        when {
                            !petResponse.isSuccessful -> {
                                error = "ข้อผิดพลาดในการโหลดข้อมูลสัตว์เลี้ยง: ${petResponse.code()} - ${petResponse.message()}"
                            }
                            !roomResponse.isSuccessful -> {
                                error = "ข้อผิดพลาดในการโหลดข้อมูลห้อง: ${roomResponse.code()} - ${roomResponse.message()}"
                            }
                            else -> {
                                error = "ข้อผิดพลาดในการโหลดข้อมูลการจอง: ${bookingResponse.code()} - ${bookingResponse.message()}"
                            }
                        }
                        Log.e("AdminDashboard", "API Error: $error")
                    }
                    isLoading = false
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                error = "เกิดข้อผิดพลาด: ${e.message}"
                Log.e("AdminDashboard", "Exception: ${e.message}", e)
                isLoading = false
            }
        }
    }
    // UI Components
    Scaffold { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            ErrorDisplay(error = error!!, paddingValues = paddingValues)
        } else {
            when (selectedTab) {
                0 -> DashboardTab(
                    paddingValues = paddingValues,
                    totalPets = totalPets,
                    availableRooms = availableRooms,
                    activeBookings = activeBookings,
                    pendingCheckouts = pendingCheckouts,
                    todayCheckIns = todayCheckIns,
                    roomStatistics = roomStatistics,
                    recentBookings = recentBookings,
                    onBookingClick = {
                        selectedTab = 1
                    },
                    navController = navController
                )
                1 -> BookingsTab(
                    paddingValues = paddingValues,
                    bookings = bookings,
                    onBookingClick = {
                        selectedTab = 2
                    }
                )
                2 -> RoomsTab(
                    paddingValues = paddingValues,
                    rooms = rooms,
                    onRoomClick = { /* Handle room click */ }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardTab(
    paddingValues: PaddingValues,
    totalPets: Int,
    availableRooms: Int,
    activeBookings: Int,
    pendingCheckouts: Int,
    todayCheckIns: Int,
    roomStatistics: List<RoomStatistic>,
    recentBookings: List<Booking>,
    onBookingClick: (Int) -> Unit,
    navController: NavHostController
) {
    val context = LocalContext.current
    val sharePreferences = remember { SharePreferencesManager(context) }

    var logoutAlert by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Quick stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    painterResource(id = R.drawable.animals),
                    title = "สัตว์เลี้ยงทั้งหมด",
                    value = "$totalPets ตัว",
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    painterResource(id = R.drawable.open_door),
                    title = "ห้องว่าง",
                    value = "$availableRooms ห้อง",
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }

        // Booking Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BookingStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.CheckCircle,
                    title = "การจองที่ยังอยู่",
                    value = "$activeBookings",
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )
                BookingStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.ExitToApp,
                    title = "จะออกเร็วๆ นี้",
                    value = "$pendingCheckouts",
                    color = MaterialTheme.colorScheme.errorContainer
                )
                BookingStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.AccountBox,
                    title = "เช็คอินวันนี้",
                    value = "$todayCheckIns",
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        // Room status
        item {
            DashboardCard(
                title = "สถานะห้องพัก",
                icon = Icons.Default.Home
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "ประเภท",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                "ว่าง",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "จอง",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                "ทำความสะอาด",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFFC107)
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Room stats
                    roomStatistics.forEach { stat ->
                        RoomTypeRow(
                            type = stat.type,
                            available = "${stat.available}",
                            booked = "${stat.booked}",
                            clean = "${stat.clean}"
                        )
                    }
                }
            }
        }

        // Recent bookings
        item {
            DashboardCard(
                title = "การจองล่าสุด",
                icon = Icons.Default.DateRange,
                actionText = "ดูทั้งหมด"
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (recentBookings.isEmpty()) {
                        Text(
                            "ไม่มีการจองล่าสุด",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        recentBookings.forEach { booking ->
                            RecentBookingItem(
                                booking = booking,
                                onClick = { onBookingClick(booking.bookingId) }
                            )
                        }
                    }
                    Button(
                        onClick = {
                            logoutAlert = true
                        },
                        modifier = Modifier.fillMaxWidth().padding(20.dp)
                    ) {
                        Text("Logout")
                    }
                }
            }
        }


    }

    if(logoutAlert){
        AlertDialog(
            onDismissRequest = {logoutAlert = false},
            title = {
                Text("ออกจากระบบ")
            },
            text = {
                Text("คุณต้องการออกจากระบบหรือไม่?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        sharePreferences.clearUserAll()
                        navController.navigate(ScreenLogin.Login.route)
                    }

                ) {
                    Text("ตกลง")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        logoutAlert = false
                    }

                ) {
                    Text("ยกเลิก")
                }
            }
        )
    }
}
@Composable
fun BookingsTab(
    paddingValues: PaddingValues,
    bookings: List<Booking>,
    onBookingClick: (Int) -> Unit
) {
    var selectedStatusFilter by remember { mutableStateOf("ทั้งหมด") }
    val statusOptions = listOf("ทั้งหมด", "รอเช็คอิน", "เข้าพักอยู่", "สำเร็จ", "ยกเลิก")

    var searchQuery by remember { mutableStateOf("") }
    var filteredBookings by remember { mutableStateOf(bookings) }

    LaunchedEffect(bookings, selectedStatusFilter, searchQuery) {
        filteredBookings = bookings.filter { booking ->
            // Status filter
            val statusMatches = when (selectedStatusFilter) {
                "รอเช็คอิน" -> booking.status == 0
                "เข้าพักอยู่" -> booking.status == 1
                "สำเร็จ" -> booking.status == 2
                "ยกเลิก" -> booking.status == 3
                else -> true // "ทั้งหมด"
            }

            // Search query filter
            val queryMatches = searchQuery.isEmpty() ||
                    booking.name.contains(searchQuery, ignoreCase = true) ||
                    booking.petName.contains(searchQuery, ignoreCase = true) ||
                    booking.tellNumber.contains(searchQuery, ignoreCase = true)

            statusMatches && queryMatches
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ค้นหาชื่อ, สัตว์เลี้ยง หรือเบอร์โทร") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "ค้นหา") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Status filter
        ScrollableTabRow(
            selectedTabIndex = statusOptions.indexOf(selectedStatusFilter),
            edgePadding = 0.dp,
            divider = {}
        ) {
            statusOptions.forEachIndexed { index, status ->
                Tab(
                    selected = selectedStatusFilter == status,
                    onClick = { selectedStatusFilter = status },
                    text = { Text(status) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bookings list
        if (filteredBookings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        "ไม่พบการจองที่ตรงกับเงื่อนไข",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredBookings) { booking ->
                    BookingListItem(
                        booking = booking,
                        onClick = { onBookingClick(booking.bookingId) }
                    )
                }
            }
        }
    }
}

@Composable
fun RoomsTab(
    paddingValues: PaddingValues,
    rooms: List<Room>,
    onRoomClick: (Int) -> Unit
) {
    var selectedStatusFilter by remember { mutableStateOf("ทั้งหมด") }
    val statusOptions = listOf("ทั้งหมด", "ว่าง", "จอง", "ทำความสะอาด")

    var selectedPetTypeFilter by remember { mutableStateOf("ทั้งหมด") }
    val petTypes = listOf("ทั้งหมด") + rooms.map { it.pet_type }.distinct()

    var filteredRooms by remember { mutableStateOf(rooms) }

    LaunchedEffect(rooms, selectedStatusFilter, selectedPetTypeFilter) {
        filteredRooms = rooms.filter { room ->
            // Status filter
            val statusMatches = when (selectedStatusFilter) {
                "ว่าง" -> room.room_status == 0
                "จอง" -> room.room_status == 1
                "ทำความสะอาด" -> room.room_status == 2
                else -> true // "ทั้งหมด"
            }

            // Pet type filter
            val petTypeMatches = selectedPetTypeFilter == "ทั้งหมด" ||
                    room.pet_type == selectedPetTypeFilter

            statusMatches && petTypeMatches
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // Status filter
        ScrollableTabRow(
            selectedTabIndex = statusOptions.indexOf(selectedStatusFilter),
            edgePadding = 0.dp,
            divider = {}
        ) {
            statusOptions.forEachIndexed { index, status ->
                Tab(
                    selected = selectedStatusFilter == status,
                    onClick = { selectedStatusFilter = status },
                    text = { Text(status) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pet type filter
        if (petTypes.size > 2) { // Only show if we have multiple pet types
            ScrollableTabRow(
                selectedTabIndex = petTypes.indexOf(selectedPetTypeFilter),
                edgePadding = 0.dp,
                divider = {}
            ) {
                petTypes.forEachIndexed { index, type ->
                    Tab(
                        selected = selectedPetTypeFilter == type,
                        onClick = { selectedPetTypeFilter = type },
                        text = { Text(type) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rooms grid
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredRooms.chunked(2)) { rowRooms ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowRooms.forEach { room ->
                        RoomGridItem(
                            modifier = Modifier.weight(1f),
                            room = room,
                            onClick = { onRoomClick(room.room_id) }
                        )
                    }

                    // Add empty spaces if needed to keep the grid layout
                    if (rowRooms.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun RoomGridItem(
    modifier: Modifier = Modifier,
    room: Room,
    onClick: () -> Unit
) {
    val statusColor = when (room.room_status) {
        0 -> MaterialTheme.colorScheme.primary // Available
        1 -> MaterialTheme.colorScheme.error // Booked
        2 -> Color(0xFFFFC107) // Cleaning
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusText = when (room.room_status) {
        0 -> "ว่าง"
        1 -> "ไมว่าง"
        2 -> "ทำความสะอาด"
        else -> "ไม่ทราบสถานะ"
    }

    Card(
        modifier = modifier
            .height(180.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Room number and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ห้อง ${room.room_id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Room details
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "ประเภท: ${room.room_type}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "สำหรับ: ${room.pet_type}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "ราคา: ${room.price_per_day} บาท/วัน",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Quick action button
            if (room.room_status == 0) { // Available
                Button(
                    onClick = { /* Handle booking */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("จองเลย")
                }
            } else if (room.room_status == 1) { // Booked
                OutlinedButton(
                    onClick = { /* Handle view details */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ดูรายละเอียด")
                }
            } else { // Cleaning
                Button(
                    onClick = { /* Handle mark as available */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC107)
                    )
                ) {
                    Text("ทำความสะอาดเสร็จ")
                }
            }
        }
    }
}

@Composable
fun ErrorDisplay(
    error: String,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { /* Retry loading */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("ลองใหม่อีกครั้ง")
            }
        }
    }
}


// Supporting components
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BookingStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (actionText != null) {
                    TextButton(onClick = onActionClick) {
                        Text(
                            text = actionText,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
fun RoomTypeRow(
    type: String,
    available: String,
    booked: String,
    clean: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = type,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = available,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = booked,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = clean,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC107),
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RecentBookingItem(
    booking: Booking,
    onClick: () -> Unit
) {
    val statusColor = when (booking.status) {
        0 -> Color(0xFF3F51B5) // รอเช็คอิน
        1 -> Color(0xFF4CAF50) // เข้าพักอยู่
        2 -> Color(0xFF9E9E9E) //เช็คเอาท์
        3 -> Color(0xFFF44336) // ยกเลิก
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusText = when (booking.status) {
        0 -> "รอเช็คอิน"
        1 -> "เข้าพักอยู่"
        2 -> "เช็คเอาท์"
        3 -> "ยกเลิก"
        else -> "ไม่ทราบสถานะ"
    }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val checkInDate = try {
        LocalDate.parse(booking.checkIn.split("T")[0], dateFormatter)
    } catch (e: Exception) {
        null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(statusColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Booking details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${booking.name} (${booking.petName})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "เช็คอิน: ${checkInDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: booking.checkIn}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusColor.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = text)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
@Composable
fun BookingListItem(
    booking: Booking,
    onClick: () -> Unit
) {
    val statusColor = when (booking.status) {
        0 -> Color(0xFF3F51B5) // รอเช็คอิน
        1 -> Color(0xFF4CAF50) // เข้าพักอยู่
        2 -> Color(0xFF9E9E9E) // สำเร็จ
        3 -> Color(0xFFF44336) // ยกเลิก
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val statusText = when (booking.status) {
        0 -> "รอเช็คอิน"
        1 -> "เข้าพักอยู่"
        2 -> "สำเร็จ"
        3 -> "ยกเลิก"
        else -> "ไม่ทราบสถานะ"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Customer and pet name
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "สัตว์เลี้ยง: ${booking.petName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Status tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Booking details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BookingDetailItem(
                    icon = Icons.Default.Phone,
                    label = "เบอร์โทร",
                    value = booking.tellNumber
                )

                BookingDetailItem(
                    icon = Icons.Default.DateRange,
                    label = "เช็คอิน",
                    value = booking.checkIn.split("T")[0]
                )

                BookingDetailItem(
                    icon = Icons.Default.ExitToApp,
                    label = "เช็คเอาท์",
                    value = booking.checkOut.split("T")[0]
                )
            }

            // Action buttons depend on status
            if (booking.status == 0) { // รอเช็คอิน
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { /* Handle check-in */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("เช็คอิน")
                    }

                    OutlinedButton(
                        onClick = { /* Handle cancel */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("ยกเลิก")
                    }
                }
            } else if (booking.status == 1) { // เข้าพักอยู่
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Handle check-out */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("เช็คเอาท์")
                }
            }
        }
    }
}

@Composable
fun BookingDetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}