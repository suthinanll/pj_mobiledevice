package com.example.ass07.admin.booking
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun Booking(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("ทั้งหมด") }
    var bookingList by remember { mutableStateOf<List<Booking>>(emptyList()) }

    val context = LocalContext.current
    val bookingService = BookingAPI.create()

    // โหลดข้อมูลการจองจาก API
    LaunchedEffect(Unit) {
        fetchBookings(bookingService) { bookings ->
            Log.d("Booking", "Fetched bookings: $bookings")
            bookingList = bookings
        }
    }

    // แปลงค่า selectedStatus เป็นตัวเลขสำหรับการ query
    val statusValue = when (selectedStatus) {
        "ยังไม่เช็คอิน" -> "0"
        "เช็คอินแล้ว" -> "1"
        "เช็คเอาท์แล้ว" -> "2"
        "ยกเลิก" -> "3"
        else -> "ทั้งหมด"
    }

    // กรองข้อมูลตามสถานะและคำค้นหา
    val filteredBookings = bookingList.filter {
        (statusValue == "ทั้งหมด" || it.status?.toString() == statusValue) &&
                (it.petName?.contains(searchQuery, ignoreCase = true) == true ||
                        it.name?.contains(searchQuery, ignoreCase = true) == true ||
                        it.roomType?.contains(searchQuery, ignoreCase = true) == true ||
                        it.bookingId?.toString()?.contains(searchQuery) == true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "การจองที่พักสัตว์เลี้ยง", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // ช่องค้นหา
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(" ค้นหาการจอง... (ชื่อสัตว์เลี้ยง / เจ้าของ / ประเภทห้อง)", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown เลือกสถานะการจอง
        StatusDropdown(selectedStatus) { newStatus ->
            selectedStatus = newStatus
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(filteredBookings) { booking ->
                BookingItem(booking, navController, bookingService)
            }
        }
    }
}

@Composable
fun StatusDropdown(selectedStatus: String, onStatusSelected: (String) -> Unit) {
    val statusOptions = listOf(
        "ทั้งหมด",
        "ยังไม่เช็คอิน",
        "เช็คอินแล้ว",
        "เช็คเอาท์แล้ว",
        "ยกเลิก"
    )
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("สถานะ: $selectedStatus")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            statusOptions.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ฟังก์ชันโหลดข้อมูลจาก API
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


// แสดงรายการการจองแต่ละรายการ
@Composable
fun BookingItem(booking: Booking, navController: NavController, bookingService: BookingAPI) {
    // คำนวณจำนวนวันจาก pricePerDay และ totalPay
    val numOfDays = if (booking.pricePerDay != 0 && booking.pay != 0 && booking.pricePerDay > 0) {
        booking.pay / booking.pricePerDay
    } else {
        0
    }

    // ราคารวมที่มีอยู่แล้ว
    val totalPrice = booking.totalPay ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .let { baseModifier ->
                if (booking.status != 0 ) {
                    baseModifier.clickable {
                        navController.navigate("booking_detail/${booking.bookingId}")
                    }
                } else {
                    baseModifier
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "📌 ID: ${booking.bookingId ?: "ไม่ทราบ"}")
            Text(text = "🐶 สัตว์เลี้ยง: ${booking.petName ?: "ไม่มีข้อมูล"} (${booking.petNameType ?: "ไม่ระบุ"} - ${booking.petBreed ?: "ไม่ระบุ"}, ${booking.petAge ?: "?"} ปี)")
            Text(text = "👤 เจ้าของ: ${booking.name ?: "ไม่ระบุ"} (${booking.tellNumber ?: "ไม่มีเบอร์"})")
            Text(text = "🏠 ห้อง: ${booking.roomType ?: "ไม่ระบุ"} (ราคา ${booking.pricePerDay ?: "?"} บาท/วัน)")
            Text(text = "📅 Check-in: ${booking.checkIn ?: "ไม่ระบุ"}")
            Text(text = "📅 Check-out: ${booking.checkOut ?: "ไม่ระบุ"}")
            Text(text = "📅 จำนวนวันที่เข้าพัก: $numOfDays วัน")
            Text(text = "💰 ราคารวม: $totalPrice บาท")
            StatusText(booking.status) // status แบบมีสี

            Row(horizontalArrangement = Arrangement.End) {
                if (booking.status == 0) {
                    Button(
                        onClick = { onConfirmBooking(booking.bookingId) },
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "เช็คอินเข้าพัก")
                    }

                    Spacer(modifier = Modifier.padding(6.dp))

                    Button(
                        onClick = { onCancelBooking(booking.bookingId, bookingService) },
                        modifier = Modifier.padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "ยกเลิกการจอง")
                    }
                }
            }
        }
    }
}


// onclick เช็นอิน
fun onConfirmBooking(bookingId: Int) {
    val api = BookingAPI.create()
    val statusUpdate = mapOf("booking_status" to 1)

    // เพิ่ม log เพื่อดูข้อมูลที่จะส่ง
    Log.d("BookingDetail", "Sending status update: $statusUpdate for booking $bookingId")

    api.updateBooking(bookingId, statusUpdate).enqueue(object : Callback<Map<String, String>> {
        override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
            // เพิ่ม log เพื่อดู response code และ body
            Log.d("BookingDetail", "Response code: ${response.code()}")
            Log.d("BookingDetail", "Response body: ${response.body()}")

            if (response.isSuccessful) {
                Log.d("BookingDetail", "อัปเดตสถานะสำเร็จ: ${response.body()?.get("message")}")
            } else {
                Log.e("BookingDetail", "อัปเดตสถานะล้มเหลว: ${response.message()}")
                // เพิ่ม log เพื่อดู error body
                Log.e("BookingDetail", "Error body: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
            Log.e("BookingDetail", "API call failed: ${t.message}")
        }
    })
}

// onclick ยกเลิก
fun onCancelBooking(bookingId: Int, bookingService: BookingAPI) {
    val statusUpdate = mapOf("booking_status" to 3)  // เปลี่ยนเป็น booking_status

    bookingService.updateBooking(bookingId, statusUpdate).enqueue(object : Callback<Map<String, String>> {
        override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
            if (response.isSuccessful) {
                Log.d("Booking", "สถานะการจองยกเลิกสำเร็จ: ${response.body()?.get("message")}")
            } else {
                Log.e("Booking", "การยกเลิกสถานะล้มเหลว: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
            Log.e("Booking", "API call failed: ${t.message}")
        }
    })
}

@Composable
fun StatusText(status: Int) {
    val (statusText, statusColor) = when (status) {
        0 -> "ยังไม่เช็คอิน" to MaterialTheme.colorScheme.primary
        1 -> "เช็คอินแล้ว" to Color(0xFF4CAF50) // สีเขียว
        2 -> "เช็คเอาท์แล้ว" to Color(0xFF2196F3) // สีน้ำเงิน
        3 -> "ยกเลิก" to Color(0xFFE91E63) // สีแดง
        else -> "ไม่ระบุ" to Color.Gray
    }

    Text(
        text = "📌 สถานะ: $statusText",
        color = statusColor,
        fontWeight = FontWeight.Bold
    )
}



