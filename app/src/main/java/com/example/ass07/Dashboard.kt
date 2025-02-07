package com.example.ass07

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ass07.ui.theme.PetLodgingTheme

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetLodgingTheme {
                DashboardScreen()
            }
        }
    }
}

@Composable
fun DashboardScreen() {
    // State สำหรับจำนวนห้องว่างสำหรับสัตว์ชนิดต่างๆ
    val availableCatRooms = remember { mutableStateOf(3) }
    val availableDogRooms = remember { mutableStateOf(2) }
    val availableRabbitRooms = remember { mutableStateOf(4) }
    val context = LocalContext.current

    // ใช้ Box เป็นพื้นหลังที่มี gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // หัวข้อหน้าจอ
            Text(
                text = "แดชบอร์ด",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Card สำหรับแสดงสถานะ "จำนวนห้องว่าง" ของสัตว์ชนิดต่างๆ
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                shape = RoundedCornerShape(20.dp), // ปรับขอบมนมากขึ้น
                elevation = CardDefaults.cardElevation(12.dp) // เพิ่ม elevation เพื่อให้เด่นขึ้น
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(20.dp), // เพิ่ม padding ภายใน Card
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "จำนวนห้องว่างสำหรับแมว: ${availableCatRooms.value}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(5.dp)) // ลด spacer ระหว่างบรรทัด
                    Text(
                        text = "จำนวนห้องว่างสำหรับสุนัข: ${availableDogRooms.value}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "จำนวนห้องว่างสำหรับกระต่าย: ${availableRabbitRooms.value}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp)) // เว้นระยะห่างระหว่าง Card กับปุ่มต่างๆ
            // ปุ่มสำหรับเมนูต่างๆ (แบบใหม่) พร้อมตกแต่งสีเหลืองและสีส้ม
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DashboardButton(
                    text = "จัดการห้องพัก",
                    imageResId = R.drawable.logoapp,
                    onClick = {
                        Toast.makeText(context, "จัดการห้องพัก ถูกคลิก", Toast.LENGTH_SHORT).show()
                    }
                )
                DashboardButton(
                    text = "จัดการการจอง",
                    imageResId = R.drawable.logoapp,
                    onClick = {
                        Toast.makeText(context, "จัดการการจอง ถูกคลิก", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            Spacer(modifier = Modifier.height(5.dp)) // เว้นระยะห่างระหว่างแถว
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DashboardButton(
                    text = "สรุปวันนี้",
                    imageResId = R.drawable.logoapp,
                    onClick = {
                        Toast.makeText(context, "สรุปวันนี้ ถูกคลิก", Toast.LENGTH_SHORT).show()
                    }
                )
                DashboardButton(
                    text = "รายงานสถิติ",
                    imageResId = R.drawable.logoapp,
                    onClick = {
                        Toast.makeText(context, "รายงานสถิติ ถูกคลิก", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun DashboardButton(text: String, imageResId: Int, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .size(150.dp) // ขนาดปุ่มสี่เหลี่ยมจัตุรัส
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(

                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    PetLodgingTheme {
        DashboardScreen()
    }
}
