package com.example.ass07

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaymentScreen(padding: androidx.compose.ui.Modifier) {
    Column(
        modifier = androidx.compose.ui.Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.1f))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(90.dp))
        // Header
        Row(
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = androidx.compose.ui.Modifier.width(16.dp))

        }

        Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = androidx.compose.ui.Modifier.padding(16.dp)
            ) {
                Text(
                    text = "รายละเอียดห้องพัก",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = androidx.compose.ui.Modifier
                            .width(80.dp)
                            .height(80.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                    }

                    Spacer(modifier = androidx.compose.ui.Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "1 x ดีลักซ์",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "8 ตารางเมตร | ห้องขนาดใหญ่ | อากาศถ่ายเท",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                Text(
                    text = "รายละเอียดการจอง",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

                Row(
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                ) {
                    Column(modifier = androidx.compose.ui.Modifier.weight(1f)) {
                        Text(text = "เช็คอิน", color = Color.Gray)
                        Text(
                            text = "06 Dec 2024",
                            fontWeight = FontWeight.Medium
                        )
                        Text(text = "12:00", color = Color.Gray)
                    }
                    Column(modifier = androidx.compose.ui.Modifier.weight(1f)) {
                        Text(text = "เช็คเอาท์", color = Color.Gray)
                        Text(
                            text = "08 Dec 2024",
                            fontWeight = FontWeight.Medium
                        )
                        Text(text = "15:00", color = Color.Gray)
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
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1 ห้อง 2 คืน",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "THB 1,520",
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ภาษีและธรรมเนียม",
                        modifier = androidx.compose.ui.Modifier.weight(1f)
                    )
                    Text(
                        text = "THB 0",
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                Row(
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ราคารวมทั้งสิ้น",
                        fontWeight = FontWeight.Bold,
                        modifier = androidx.compose.ui.Modifier.weight(1f)
                    )
                    Text(
                        text = "THB 1,520",
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
                        "พร้อมเพย์",

                    )
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB74D))
                ) {
                    Text(
                        text = "ชำระเงิน",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier =Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RadioButtonGroup(options: List<String>) {
    Column {
        options.forEach { option ->
            Row(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = false,
                    onClick = { /* TODO */ }
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = option)
            }
        }
    }
}

