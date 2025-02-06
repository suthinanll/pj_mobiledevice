package com.example.ass07



import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    // State สำหรับจำนวนห้องว่าง (สมมุติเริ่มต้น 5 ห้อง)
    val availableRooms = remember { mutableStateOf(5) }
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
                .padding(16.dp),
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
            // Card สำหรับแสดงสถานะ "จำนวนห้องว่าง"
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "จำนวนห้องว่างขณะนี้: ${availableRooms.value}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            // ปุ่มสำหรับเมนูต่างๆ
            DashboardButton(
                text = "จัดการห้องพัก",
                onClick = {
                    Toast.makeText(context, "จัดการห้องพัก ถูกคลิก", Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DashboardButton(
                text = "จัดการการจอง",
                onClick = {
                    Toast.makeText(context, "จัดการการจอง ถูกคลิก", Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DashboardButton(
                text = "สรุปวันนี้",
                onClick = {
                    Toast.makeText(context, "สรุปวันนี้ ถูกคลิก", Toast.LENGTH_SHORT).show()
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            DashboardButton(
                text = "รายงานสถิติ",
                onClick = {
                    Toast.makeText(context, "รายงานสถิติ ถูกคลิก", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun DashboardButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    PetLodgingTheme {
        DashboardScreen()
    }
}
