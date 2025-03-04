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

import com.example.ass07.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.ass07.admin.booking.BookingAPI
import com.example.ass07.admin.booking.Booking
import com.example.ass07.admin.PetApi
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminDashboard(
    onNavigateToBookingDetails: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val roomAPI = remember { RoomAPI.create() }
    val bookingAPI = remember { BookingAPI.create() }
    val petAPI = remember { PetApi.create() }
    var totalRooms by remember { mutableStateOf(0) }
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

    // ฟังก์ชันสำหรับแปลงวันที่จาก ISO 8601 เป็น LocalDate
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun parseDate(dateString: String): LocalDate? {
        return try {
            // แปลงวันที่จากรูปแบบ ISO 8601 ไปเป็น ZonedDateTime ก่อน
            val instant = Instant.parse(dateString)
            // แปลงจาก Instant เป็น LocalDate โดยใช้ timezone ของระบบ
            // API Level 26+ compatible
            instant.atZone(ZoneId.systemDefault()).toLocalDate()
        } catch (e: Exception) {
            Log.e("DateParsing", "ไม่สามารถแปลงวันที่: $dateString - ${e.message}")
            null
        }
    }

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
                            totalRooms = rooms.size
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
                        if (bookingsList != null && bookingsList.isNotEmpty()) {
                            val firstBooking = bookingsList.first()
                            Log.d("BookingDebug", "First booking data:")
                            Log.d("BookingDebug", "checkIn: '${firstBooking.checkIn}'")
                            Log.d("BookingDebug", "checkOut: '${firstBooking.checkOut}'")
                            Log.d("BookingDebug", "createdAt: '${firstBooking.createdAt}'")
                            Log.d("BookingDebug", "updatedAt: '${firstBooking.updatedAt}'")

                            // ดีบักข้อมูลการจองทั้งหมด
                            Log.d("BookingDebug", "All booking date formats:")
                            bookingsList.forEachIndexed { index, booking ->
                                Log.d("BookingDebug", "Booking #$index - checkIn: '${booking.checkIn}', checkOut: '${booking.checkOut}'")
                            }
                        }

                        if (bookingsList != null) {
                            bookings = bookingsList

                            val today = LocalDate.now()

                            activeBookings = bookings.count { it.status == 1 }

                            // คำนวณ pendingCheckouts (จะออกเร็วๆนี้)
                            pendingCheckouts = bookings.count { booking ->
                                try {
                                    val checkoutDate = parseDate(booking.checkOut)
                                    if (checkoutDate != null) {
                                        val daysUntilCheckout = ChronoUnit.DAYS.between(today, checkoutDate)
                                        // Consider active bookings with checkout in next 2 days
                                        booking.status == 1 && daysUntilCheckout in 0..2
                                    } else {
                                        false
                                    }
                                } catch (e: Exception) {
                                    Log.e("CheckoutDebug", "Error calculating: ${e.message}")
                                    false
                                }
                            }

                            // คำนวณ todayCheckIns (เช็คอินวันนี้)
                            todayCheckIns = bookings.count { booking ->
                                try {
                                    val checkinDate = parseDate(booking.checkIn)
                                    checkinDate?.isEqual(today) == true && booking.status == 1
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
                    onBookingClick = onNavigateToBookingDetails,
                    totalRooms = totalRooms
                )
                1 -> BookingsTab(
                    paddingValues = paddingValues,
                    bookings = bookings,
                    onBookingClick = onNavigateToBookingDetails
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
    totalRooms: Int,
    activeBookings: Int,
    pendingCheckouts: Int,
    todayCheckIns: Int,
    roomStatistics: List<RoomStatistic>,
    recentBookings: List<Booking>,
    onBookingClick: (Int) -> Unit
) {
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
                    Horizontal = Alignment.Center,
                    painterResource(id = R.drawable.animals),
                    title = "สัตว์เลี้ยงทั้งหมด",
                    value = "$totalPets ตัว",
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    Horizontal = Alignment.Center,
                    painterResource(id = R.drawable.open_door),
                    title = "ห้องว่าง",
                    value = "$availableRooms ห้อง",
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    Horizontal = Alignment.Center,
                    painterResource(id = R.drawable.open_door),
                    title = "ห้องทั้งหมด",
                    value = "$totalRooms ห้อง",
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
                                "ไม่ว่าง",
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
                "เช็คเอาท์แล้ว" -> booking.status == 2
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
    Horizontal : Alignment,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
//การจองล่าสุด
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
@RequiresApi(Build.VERSION_CODES.O)
// ฟังก์ชันสำหรับแปลงวันที่จาก ISO 8601 เป็น LocalDate
private fun parseDate(dateString: String): LocalDate? {
    return try {
        // แปลงวันที่จากรูปแบบ ISO 8601 ไปเป็น ZonedDateTime ก่อน
        val instant = Instant.parse(dateString)
        // แปลงจาก Instant เป็น LocalDate โดยใช้ timezone ของระบบ
        // API Level 26+ compatible
        instant.atZone(ZoneId.systemDefault()).toLocalDate()
    } catch (e: Exception) {
        Log.e("DateParsing", "ไม่สามารถแปลงวันที่: $dateString - ${e.message}")
        null
    }
}