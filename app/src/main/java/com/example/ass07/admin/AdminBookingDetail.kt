package com.example.ass07.admin

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun BookingDetail(bookingId: Int) {
    val context = LocalContext.current
    val bookingApi = BookingAPI.create()
    val coroutineScope = rememberCoroutineScope()

    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // โหลดข้อมูลการจองจาก API
    LaunchedEffect(bookingId) {
        bookingApi.getBookingById(bookingId).enqueue(object : Callback<Booking> {
            override fun onResponse(call: Call<Booking>, response: Response<Booking>) {
                if (response.isSuccessful) {
                    booking = response.body()
                } else {
                    Log.e("BookingDetail", "Error: ${response.message()}")
                }
                isLoading = false
            }

            override fun onFailure(call: Call<Booking>, t: Throwable) {
                Log.e("BookingDetail", "API call failed: ${t.message}")
                isLoading = false
            }
        })
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        booking?.let {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(text = "รายละเอียดการจอง", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text("รหัสการจอง: ${it.bookingId}")
                Text("วันเช็คอิน: ${it.checkIn}")
                Text("วันเช็คเอาท์: ${it.checkOut}")
                Text("เจ้าของ: ${it.name} (${it.tellNumber})")
                Text("สัตว์เลี้ยง: ${it.petName} (${it.petBreed}, ${it.petAge} ปี)")
                Text("ห้องพัก: ${it.roomType} (ราคา ${it.pricePerDay} บาท/วัน)")
                Text("สถานะ: ${it.status}")

                if (it.status == 0) { // 0 = รออนุมัติ
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    updateBookingStatus(it.bookingId, 1) // 1 = อนุมัติ
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                        ) {
                            Text("อนุมัติ")
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    updateBookingStatus(it.bookingId, 3) // 3 = ยกเลิก
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("ยกเลิก")
                        }
                    }
                }
            }
        } ?: Text("ไม่พบข้อมูลการจอง", modifier = Modifier.padding(16.dp))
    }
}

// ฟังก์ชันอัปเดตสถานะการจอง
suspend fun updateBookingStatus(bookingId: Int, status: Int) {
    val api = BookingAPI.create()
    api.updateBooking(bookingId, status).enqueue(object : Callback<Map<String, String>> {
        override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
            if (response.isSuccessful) {
                Log.d("BookingDetail", "อัปเดตสถานะสำเร็จ: ${response.body()?.get("message")}")
            } else {
                Log.e("BookingDetail", "อัปเดตสถานะล้มเหลว: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
            Log.e("BookingDetail", "API call failed: ${t.message}")
        }
    })
}
