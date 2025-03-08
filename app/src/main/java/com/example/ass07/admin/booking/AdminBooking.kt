package com.example.ass07.admin.booking
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ass07.admin.ScreenAdmin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import java.util.TimeZone

// // ประกาศสีหลักที่ใช้ในแอพพลิเคชัน
private val backgroundColor = Color(0xFFFFFAF0)
private val primaryColor = Color(0xFFC88141)
private val accentColor = Color(0xFFFFD966)
private val cardBackground = Color.White
private val checkInColor = Color(0xFF4CAF50)
private val checkOutColor = Color(0xFF2196F3)
private val cancelColor = Color(0xD7EE2C2C)
private val dividerColor = Color(0xFFEEE0D0)

@Composable
fun Booking(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("ทั้งหมด") }
    var selectedCheckoutFilter by remember { mutableStateOf("ทั้งหมด") }
    var bookingList by remember { mutableStateOf<List<Booking>>(emptyList()) }

    val context = LocalContext.current
    val bookingService = BookingAPI.create()

    LaunchedEffect(Unit) {
        fetchBookings(bookingService) { bookings ->
            Log.d("Booking", "Fetched bookings: $bookings")
            bookingList = bookings
        }
    }

    val filteredBookings = bookingList.filter { booking ->
        // Filter by status
        val statusMatch = when (selectedStatus) {
            "ทั้งหมด" -> true
            "ยังไม่เช็คอิน" -> booking.status?.toString() == "0"
            "เช็คอินแล้ว" -> booking.status?.toString() == "1"
            "เช็คเอาท์แล้ว" -> booking.status?.toString() == "2"
            "ยกเลิก" -> booking.status?.toString() == "3"
            else -> false
        }

        // Filter by search query
        val searchMatch = searchQuery.isEmpty() ||
                booking.petName?.contains(searchQuery, ignoreCase = true) == true ||
                booking.name?.contains(searchQuery, ignoreCase = true) == true ||
                booking.roomType?.contains(searchQuery, ignoreCase = true) == true ||
                booking.bookingId?.toString()?.contains(searchQuery) == true

        // Filter by checkout date
        val checkoutMatch = when (selectedCheckoutFilter) {
            "ทั้งหมด" -> true
            "ออกเร็วๆนี้ (2 วัน)" -> {
                // เฉพาะ booking ที่เช็คอินแล้วเท่านั้น
                if (booking.status?.toString() == "1") isCheckoutWithinDays(booking.checkOut, 2) else false
            }
            "ออกในอาทิตย์นี้" -> {
                if (booking.status?.toString() == "1") isCheckoutWithinDays(booking.checkOut, 7) else false
            }
            "ออกในสัปดาห์หน้า" -> {
                if (booking.status?.toString() == "1") isCheckoutWithinDays(booking.checkOut, 14) else false
            }
            "ออกในเดือนนี้" -> {
                if (booking.status?.toString() == "1") isCheckoutWithinDays(booking.checkOut, 30) else false
            }
            else -> true
        }


        // Combine all filters
        statusMatch && searchMatch && checkoutMatch
    }
    bookingList.forEach { booking ->
        Log.d("BookingStatus", "Booking ID: ${booking.bookingId}, Status: ${booking.status}\n")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Text(
            text = "การจองที่พักสัตว์เลี้ยง",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            placeholder = {
                Text(
                    "ค้นหาการจอง... (ชื่อสัตว์เลี้ยง / เจ้าของ / ประเภทห้อง)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = primaryColor
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = dividerColor,
                focusedContainerColor = cardBackground,
                unfocusedContainerColor = cardBackground
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Updated Status Dropdown
        StatusDropdown(
            selectedStatus = selectedStatus,
            selectedCheckoutFilter = selectedCheckoutFilter,
            onStatusSelected = { newStatus ->
                selectedStatus = newStatus
            },
            onCheckoutFilterSelected = { newFilter ->
                selectedCheckoutFilter = newFilter
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Booking count
        Text(
            text = "พบ ${filteredBookings.size} รายการ",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Booking list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredBookings) { booking ->
                BookingItem(booking, navController, bookingService, onResult = { bookings ->
                    bookingList = bookings
                })
            }
        }
    }
}

// เช็ควันที่ checkout
fun isCheckoutWithinDays(checkOutStr: String?, days: Long): Boolean {
    return try {
        val checkOutDate = LocalDate.parse(checkOutStr?.split("T")?.get(0))
        val today = LocalDate.now()
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endOfWeek = startOfWeek.plusDays(6)

        when (days) {
            2L -> { // ออกเร็วๆนี้ (2 วัน)
                val twoDaysFromNow = today.plusDays(2)
                checkOutDate in today..twoDaysFromNow
            }
            7L -> { // ออกในอาทิตย์นี้
                checkOutDate in startOfWeek..endOfWeek
            }
            14L -> { // ออกในสัปดาห์หน้า
                val nextWeekStart = endOfWeek.plusDays(1)
                val nextWeekEnd = nextWeekStart.plusDays(6)
                checkOutDate in nextWeekStart..nextWeekEnd
            }
            30L -> { // ออกในเดือนนี้
                val startOfMonth = today.withDayOfMonth(1)
                val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())
                checkOutDate in startOfMonth..endOfMonth
            }
            else -> true
        }
    } catch (e: Exception) {
        false
    }
}


@Composable
fun StatusDropdown(
    selectedStatus: String,
    selectedCheckoutFilter: String,
    onStatusSelected: (String) -> Unit,
    onCheckoutFilterSelected: (String) -> Unit
) {
    val statusOptions = listOf(
        "ทั้งหมด",
        "ยังไม่เช็คอิน",
        "เช็คอินแล้ว",
        "เช็คเอาท์แล้ว",
        "ยกเลิก"
    )

    val checkoutFilterOptions = listOf(
        "ทั้งหมด",
        "ออกเร็วๆนี้ (2 วัน)",
        "ออกในอาทิตย์นี้",
        "ออกในสัปดาห์หน้า",
        "ออกในเดือนนี้"
    )

    var expandedStatus by remember { mutableStateOf(false) }
    var expandedCheckout by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status Dropdown
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick = { expandedStatus = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryColor,
                    containerColor = cardBackground
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "สถานะ: $selectedStatus",
                        fontSize = 14.sp
                    )
                    Icon(
                        Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Dropdown Arrow"
                    )
                }
            }

            DropdownMenu(
                expanded = expandedStatus,
                onDismissRequest = { expandedStatus = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBackground)
            ) {
                statusOptions.forEach { status ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                status,
                                fontSize = 14.sp
                            )
                        },
                        onClick = {
                            onStatusSelected(status)
                            expandedStatus = false
                        }
                    )

                    if (status != statusOptions.last()) {
                        Divider(color = dividerColor, thickness = 0.5.dp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Checkout Filter Dropdown
        Box(modifier = Modifier.weight(1f)) {
            OutlinedButton(
                onClick = { expandedCheckout = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = primaryColor,
                    containerColor = cardBackground
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "ใกล้เช็คเอาท์: $selectedCheckoutFilter",
                        fontSize = 14.sp
                    )
                    Icon(
                        Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Dropdown Arrow"
                    )
                }
            }

            DropdownMenu(
                expanded = expandedCheckout,
                onDismissRequest = { expandedCheckout = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBackground)
            ) {
                checkoutFilterOptions.forEach { filter ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                filter,
                                fontSize = 14.sp
                            )
                        },
                        onClick = {
                            onCheckoutFilterSelected(filter)
                            expandedCheckout = false
                        }
                    )

                    if (filter != checkoutFilterOptions.last()) {
                        Divider(color = dividerColor, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

fun fetchBookings(bookingService: BookingAPI, onResult: (List<Booking>) -> Unit) {
    bookingService.getBookings().enqueue(object : Callback<List<Booking>> {
        override fun onResponse(call: Call<List<Booking>>, response: Response<List<Booking>>) {
            if (response.isSuccessful) {
                val bookings = response.body() ?: emptyList()
                Log.d("Booking", "API Response: $bookings")
                onResult(bookings)
            } else {
                Log.e("Booking", "Error: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<List<Booking>>, t: Throwable) {
            Log.e("Booking", "API Call Failed: ${t.message}")
        }
    })
}

@Composable
fun BookingItem(
    booking: Booking,
    navController: NavController,
    bookingService: BookingAPI,
    onResult: (List<Booking>) -> Unit
) {
    val numOfDays = calculateNumOfDays(booking.checkIn, booking.checkOut)
    val totalPrice = booking.pay + (booking.adjust ?:0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let { baseModifier ->
                if (booking.status != 0) {
                    baseModifier.clickable {
                        navController.navigate(route = ScreenAdmin.BookingDetail.route+"/${booking.bookingId}")
                    }
                } else {
                    baseModifier
                }
            },
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${booking.bookingId ?: "ไม่ทราบ"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                StatusChip(booking.status)
            }

            Divider(
                color = dividerColor,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Pet details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🐶",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${booking.petName ?: "ไม่มีข้อมูล"}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${booking.petNameType ?: "ไม่ระบุ"} - ${booking.petBreed ?: "ไม่ระบุ"}, ${booking.petAge ?: "?"} ปี",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Owner details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "👤",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "${booking.name ?: "ไม่ระบุ"}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${booking.tellNumber ?: "ไม่มีเบอร์"}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Room details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🏠",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${booking.roomType ?: "ไม่ระบุ"} (${booking.pricePerDay ?: "?"} บาท/วัน)",
                    fontSize = 14.sp
                )
            }

            Divider(
                color = dividerColor,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Date details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Check-in",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${formatDateTime(booking.checkIn)}",
                        fontSize = 14.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Check-out",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${formatDateTime(booking.checkOut)}",
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "จำนวนวันที่เข้าพัก: $numOfDays วัน",
                    fontSize = 14.sp
                )

                Text(
                    text = "฿$totalPrice",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = primaryColor
                )
            }

            // Action buttons
            if (booking.status == 0) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onCancelBooking(booking.bookingId, bookingService, onResult) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cancelColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "ยกเลิกการจอง",
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onConfirmBooking(booking.bookingId, bookingService, onResult) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "เช็คอินเข้าพัก",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: Int) {
    val (statusText, statusColor, backgroundColor) = when (status) {
        0 -> Triple("ยังไม่เช็คอิน", Color.Gray, Color(0xFFEEEEEE))
        1 -> Triple("เช็คอินแล้ว", checkInColor, Color(0xFFE8F5E9))
        2 -> Triple("เช็คเอาท์แล้ว", checkOutColor, Color(0xFFE3F2FD))
        3 -> Triple("ยกเลิก", cancelColor, Color(0xFFFFEBEE))
        else -> Triple("ไม่ระบุ", Color.Gray, Color(0xFFEEEEEE))
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = statusText,
            color = statusColor,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun onConfirmBooking(bookingId: Int, bookingService: BookingAPI, onResult: (List<Booking>) -> Unit) {
    val statusUpdate = mapOf("booking_status" to 1)

    bookingService.updateBooking(bookingId, statusUpdate).enqueue(object : Callback<Map<String, String>> {
        override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
            if (response.isSuccessful) {
                Log.d("BookingDetail", "อัปเดตสถานะสำเร็จ: ${response.body()?.get("message")}")
                fetchBookings(bookingService, onResult)
            } else {
                Log.e("BookingDetail", "อัปเดตสถานะล้มเหลว: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
            Log.e("BookingDetail", "API call failed: ${t.message}")
        }
    })
}

fun onCancelBooking(bookingId: Int, bookingService: BookingAPI, onResult: (List<Booking>) -> Unit) {
    val statusUpdate = mapOf("booking_status" to 3)

    bookingService.updateBooking(bookingId, statusUpdate).enqueue(object : Callback<Map<String, String>> {
        override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
            if (response.isSuccessful) {
                Log.d("Booking", "สถานะการจองยกเลิกสำเร็จ: ${response.body()?.get("message")}")
                fetchBookings(bookingService, onResult)
            } else {
                Log.e("Booking", "การยกเลิกสถานะล้มเหลว: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
            Log.e("Booking", "API call failed: ${t.message}")
        }
    })
}


fun formatDateTime(dateTimeStr: String): String {
    return try {
        // รูปแบบแรก yyyy-MM-dd HH:mm:ss
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormatter.parse(dateTimeStr)

        // ใช้รูปแบบเดียวกับโค้ดด้านบน แต่เพิ่ม HH:mm สำหรับเวลา
        val outputFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        date?.let { outputFormatter.format(it) } ?: dateTimeStr
    } catch (e: Exception) {
        try {
            // ลองรูปแบบ ISO หากรูปแบบแรกไม่สำเร็จ
            // ต้องแปลง ISO format เป็น SimpleDateFormat pattern
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormatter.timeZone = TimeZone.getTimeZone("UTC") // ISO มักอยู่ในรูปแบบ UTC
            val date = inputFormatter.parse(dateTimeStr)

            val outputFormatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            date?.let { outputFormatter.format(it) } ?: dateTimeStr
        } catch (e: Exception) {
            // คืนค่าเดิมหากไม่สามารถแปลงได้
            dateTimeStr
        }
    }
}

fun calculateNumOfDays(checkIn: String, checkOut: String): Long {
    return try {
        val checkInDate = LocalDate.parse(checkIn.split("T")[0])
        val checkOutDate = LocalDate.parse(checkOut.split("T")[0])
        ChronoUnit.DAYS.between(checkInDate, checkOutDate)
    } catch (e: Exception) {
        0 // กรณีที่คำนวณไม่ได้ให้ส่ง 0 วันกลับไป
    }
}