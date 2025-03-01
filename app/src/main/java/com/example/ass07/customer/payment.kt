package com.example.ass07.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ass07.R
import com.example.ass07.admin.Room

@Composable
fun PaymentScreen(
    navController: NavController
) {

    // ดึงข้อมูลจาก savedStateHandle

    val totalPrice = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("totalPrice") ?: 0.0
    val room = navController.previousBackStackEntry?.savedStateHandle?.get<Room>("room_data")
    val pet = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("pet") ?: 0
    val checkIn = navController.previousBackStackEntry?.savedStateHandle?.get<String>("checkIn") ?: "N/A"
    val checkOut = navController.previousBackStackEntry?.savedStateHandle?.get<String>("checkOut") ?: "N/A"

    val petType = navController.previousBackStackEntry?.savedStateHandle?.get<String>("petType") ?: ""


    val checkin = navController.previousBackStackEntry?.savedStateHandle?.get<String>("checkin")
    val checkout  = navController.previousBackStackEntry?.savedStateHandle?.get<String>("checkout")

    val bookingData = navController.previousBackStackEntry?.savedStateHandle?.get<BookingClass>("booking_data")

    val days = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("days") ?: 1

    val formattedCheckIn = convertDateToMonthName(checkin ?: "")
    val formattedCheckOut = convertDateToMonthName(checkout ?: "")
    val petTypeName = when (pet ?: 0) {
        1 -> "สุนัข"
        2 -> "แมว"
        3 -> "นก"
        else -> "ไม่ทราบ"
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "ประเภท: $petType", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "$checkIn - $checkOut", fontSize = 14.sp, color = Color.Gray)

            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "รายละเอียดห้องพัก",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {}

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "$days วัน",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "รายละเอียดการจอง",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "เช็คอิน ", color = Color.Gray)
                        Text(
                            text = checkIn,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "เช็คเอาท์", color = Color.Gray)
                        Text(
                            text = checkOut,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ข้อมูลการชำระเงิน",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$days วัน ",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "THB ${String.format("%,.2f", totalPrice)}",
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ภาษีและธรรมเนียม",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "THB 0",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ราคารวมทั้งสิ้น",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "THB $totalPrice",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ช่องทางการชำระเงิน",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                RadioButtonGroup(
                    options = listOf(
                        "เงินสด",
                        "พร้อมเพย์"
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {navController.navigate(Screen.History.route) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D))
                ) {
                    Text(
                        text = "ชำระเงิน",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RadioButtonGroup(options: List<String>) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Column {
        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedOption == option),
                    onClick = { selectedOption = option }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option)
            }
        }

        if (selectedOption == "พร้อมเพย์") {
            Image(
                painter = painterResource(id = R.drawable.payqr),
                contentDescription = "QR พร้อมเพย์",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

