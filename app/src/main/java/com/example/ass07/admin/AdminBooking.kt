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



@Composable
fun Booking(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("ทั้งหมด") }
    var selectedRoomType by remember { mutableStateOf("ทั้งหมด") }

    val statusOptions = listOf("ทั้งหมด", "รออนุมัติ", "ยืนยันแล้ว", "ยกเลิก")
    val roomTypeOptions = listOf("ทั้งหมด", "ห้องมาตรฐาน", "ห้อง VIP", "ห้องรวม")

    val bookingList = listOf(
        BookingData("1", "สมชาย สุขใจ", "เจ้าตูบ", "10 ก.พ. - 12 ก.พ.", "รออนุมัติ", "ห้อง VIP"),
        BookingData("2", "มานี มั่งมี", "น้องเหมียว", "15 ก.พ. - 20 ก.พ.", "ยืนยันแล้ว", "ห้องมาตรฐาน"),
        BookingData("3", "สมปอง รักสัตว์", "เจ้าโกโก้", "18 ก.พ. - 22 ก.พ.", "รออนุมัติ", "ห้องรวม"),
        BookingData("4", "อนงค์นาถ สบายใจ", "น้องปุย", "20 ก.พ. - 25 ก.พ.", "ยกเลิก", "ห้อง VIP"),
    )

    val filteredBookings = bookingList.filter {
        (selectedStatus == "ทั้งหมด" || it.status == selectedStatus) &&
                (selectedRoomType == "ทั้งหมด" || it.roomType == selectedRoomType) &&
                (it.customer.contains(searchQuery, ignoreCase = true) || it.petName.contains(searchQuery, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "การจองที่พักสัตว์เลี้ยง", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // ช่องค้นหา
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("ค้นหาการจอง...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown สำหรับกรองสถานะ
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DropdownSelector(label = "สถานะ", options = statusOptions, selectedOption = selectedStatus) {
                selectedStatus = it
            }
            DropdownSelector(label = "ประเภทห้อง", options = roomTypeOptions, selectedOption = selectedRoomType) {
                selectedRoomType = it
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(filteredBookings) { booking ->
                BookingItem(booking, navController)
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
fun BookingItem(booking: BookingData, navController: NavController) {
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
            Text(text = "${booking.id} - ${booking.customer}", fontWeight = FontWeight.Bold)
            Text(text = "สัตว์เลี้ยง: ${booking.petName}")
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
                        Toast.makeText(context, "อนุุมัติเรียบร้อย", Toast.LENGTH_SHORT).show()
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)) {
                        Text("อนุมัติ")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        Toast.makeText(context, "ยกเลิกเรียบร้อย", Toast.LENGTH_SHORT).show()
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                        Text("ยกเลิก")
                    }
                }
            }
        }
    }
}

data class BookingData(
    val id: String,
    val customer: String,
    val petName: String,
    val date: String,
    val status: String,
    val roomType: String
)