package com.example.ass07

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Booking(bookingId: String) {
    val booking = getBookingById(bookingId) // ฟังก์ชันจำลองหาข้อมูลจากฐานข้อมูล

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "รายละเอียดการจอง", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        booking?.let {
            Text("รหัสการจอง: ${it.id}")
            Text("ลูกค้า: ${it.customer}")
            Text("สัตว์เลี้ยง: ${it.petName}")
            Text("ช่วงเวลา: ${it.date}")
            Text("ประเภทห้อง: ${it.roomType}")
            Text("สถานะ: ${it.status}")

            if (it.status == "ยืนยันแล้ว") {
                Button(onClick = { /* จัดการเช็คอิน */ }) {
                    Text("ปุ่มขยายนู่นนี่นั่นค่อยมาทำ")
                }
            }
        } ?: Text("ไม่พบข้อมูลการจอง")
    }
}

fun getBookingById(id: String): BookingData? {
    val mockData = listOf(
        BookingData("1", "สมชาย สุขใจ", "เจ้าตูบ", "10 ก.พ. - 12 ก.พ.", "ยืนยันแล้ว", "ห้อง VIP"),
        BookingData("2", "มานี มั่งมี", "น้องเหมียว", "15 ก.พ. - 20 ก.พ.", "ยืนยันแล้ว", "ห้องมาตรฐาน"),
        BookingData("3", "สมปอง รักสัตว์", "เจ้าโกโก้", "18 ก.พ. - 22 ก.พ.", "รออนุมัติ", "ห้องรวม"),
        BookingData("4", "อนงค์นาถ สบายใจ", "น้องปุย", "20 ก.พ. - 25 ก.พ.", "ยกเลิก", "ห้อง VIP")
    )
    return mockData.find { it.id == id }
}
