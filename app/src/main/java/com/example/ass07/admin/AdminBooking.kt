package com.example.ass07.admin
import android.widget.Toast
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Composable
fun Booking(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("ทั้งหมด") }
    var selectedRoomType by remember { mutableStateOf("ทั้งหมด") }
    var bookingList by remember { mutableStateOf<List<BookingData>>(emptyList()) }

    val context = LocalContext.current
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/") // เปลี่ยนเป็น URL จริง
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val bookingService = retrofit.create(BookingAPI::class.java)

    LaunchedEffect(Unit) {
        bookingService.getBookings().enqueue(object : Callback<List<Booking>> {
            override fun onResponse(call: Call<List<Booking>>, response: Response<List<Booking>>) {
                if (response.isSuccessful) {
                    bookingList = response.body() ?: emptyList()
                }
            }
            override fun onFailure(call: Call<List<Booking>>, t: Throwable) {
                Toast.makeText(context, "โหลดข้อมูลล้มเหลว", Toast.LENGTH_SHORT).show()
            }
        })
    }

    val filteredBookings = bookingList.filter {
        (selectedStatus == "ทั้งหมด" || it.status == selectedStatus) &&
                (selectedRoomType == "ทั้งหมด" || it.roomType == selectedRoomType) &&
                (it.customer.contains(searchQuery, ignoreCase = true) || it.petName.contains(searchQuery, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "การจองที่พักสัตว์เลี้ยง", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ค้นหาการจอง...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DropdownSelector(label = "สถานะ", options = listOf("ทั้งหมด", "รออนุมัติ", "ยืนยันแล้ว", "ยกเลิก"), selectedOption = selectedStatus) {
                selectedStatus = it
            }
            DropdownSelector(label = "ประเภทห้อง", options = listOf("ทั้งหมด", "ห้องมาตรฐาน", "ห้อง VIP", "ห้องรวม"), selectedOption = selectedRoomType) {
                selectedRoomType = it
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(filteredBookings) { booking ->
                BookingItem(booking, navController, bookingService) { updatedBookingList ->
                    bookingList = updatedBookingList
                }
            }
        }
    }
}

@Composable
fun DropdownSelector(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.wrapContentSize()) {
        OutlinedButton(onClick = { expanded = true }) {
            Text(text = "$label: $selectedOption")
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking, navController: NavController, bookingService: BookingAPI, onUpdate: (List<Booking>) -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(enabled = booking.status != "รออนุมัติ") {
                navController.navigate(ScreenAdmin.BookingDetail.route + "/${booking.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${booking.bookingId} - ${booking.users.name}", fontWeight = FontWeight.Bold)
            Text(text = "สัตว์เลี้ยง: ${booking.pet_name}")
            Text(text = "วันที่: ${booking.date}")
            Text(text = "ประเภทห้อง: ${booking.roomType}")
            Text(
                text = "สถานะ: ${booking.status}",
                color = when (booking.status) {
                    "รออนุมัติ" -> Color.Red
                    "ยืนยันแล้ว" -> Color.Green
                    "ยกเลิก" -> Color.Gray
                    else -> Color.Black
                }
            )

            if (booking.status == "รออนุมัติ") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(onClick = {
                        updateBookingStatus(booking.bookingId, "ยืนยันแล้ว", bookingService, context, onUpdate)
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                        Text("อนุมัติ")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        updateBookingStatus(booking.id, "ยกเลิก", bookingService, context, onUpdate)
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                        Text("ยกเลิก")
                    }
                }
            }
        }
    }
}

fun updateBookingStatus(bookingId: String, newStatus: String, bookingService: BookingService, context: android.content.Context, onUpdate: (List<BookingData>) -> Unit) {
    val updateData = mapOf("status" to newStatus)

    bookingService.updateBooking(bookingId, updateData).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Toast.makeText(context, "อัปเดตสถานะสำเร็จ", Toast.LENGTH_SHORT).show()
                fetchUpdatedBookings(bookingService, onUpdate)
            } else {
                Toast.makeText(context, "อัปเดตสถานะล้มเหลว", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Toast.makeText(context, "เกิดข้อผิดพลาด", Toast.LENGTH_SHORT).show()
        }
    })
}

fun fetchUpdatedBookings(bookingService: BookingService, onUpdate: (List<BookingData>) -> Unit) {
    bookingService.getBookings().enqueue(object : Callback<List<BookingData>> {
        override fun onResponse(call: Call<List<BookingData>>, response: Response<List<BookingData>>) {
            if (response.isSuccessful) {
                onUpdate(response.body() ?: emptyList())
            }
        }

        override fun onFailure(call: Call<List<BookingData>>, t: Throwable) {
            // Handle error
        }
    })
}
