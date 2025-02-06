package com.example.ass07

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun RoomManagementScreen() {
    val context = LocalContext.current // เข้าถึง context ที่ใช้ในการแสดง Toast

    var filterDialogOpen by remember { mutableStateOf(false) }

    // Storing selected options
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var selectedSort by remember { mutableStateOf<String?>(null) }

    val roomList = List(10) { it } // Example room list

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
            .background(Color(0xFFFFF3D9)) // เปลี่ยนเป็นพื้นหลังสีตามที่กำหนด
    ) {
        // Header with Title and Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            // Spacer to push items down
            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                Toast.makeText(context, "Back clicked", Toast.LENGTH_SHORT).show() // ใช้ context เพื่อแสดง Toast
            }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF3D3D3D) // ใช้สีเข้ม
                )
            }

             // ระยะห่างระหว่างปุ่ม Back และข้อความ
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
            Text(
                text = "Room Management",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.Black
                ),
                 // ให้ข้อความอยู่กลาง
                textAlign = androidx.compose.ui.text.style.TextAlign.Center // ข้อความอยู่กลาง
            )

            Spacer(modifier = Modifier.weight(1f)) // ให้ย้ายปุ่ม "Back" และข้อความให้อยู่กลาง

        }}

        Spacer(modifier = Modifier.height(16.dp))

        // Filter/Sort Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { filterDialogOpen = true }) {
                Text(
                    text = "Filter/Sort",
                    color = Color(0xFF007AFF), // สีปุ่ม Filter/Sort
                )
            }
        }

        // Show Dialog when the button is clicked
        if (filterDialogOpen) {
            AlertDialog(
                onDismissRequest = { filterDialogOpen = false },
                title = {
                    Text(
                        text = "Select Filter/Sort",
                        color = Color.Black
                    )
                },
                text = {
                    Column {
                        TextButton(onClick = {
                            selectedFilter = "Filter Option"
                            filterDialogOpen = false
                            Toast.makeText(context, "Filter selected", Toast.LENGTH_SHORT).show() // ใช้ context
                        }) {
                            Text(
                                "Filter",
                                color = Color.Black
                            )
                        }
                        TextButton(onClick = {
                            selectedSort = "Sort Option"
                            filterDialogOpen = false
                            Toast.makeText(context, "Sort selected", Toast.LENGTH_SHORT).show() // ใช้ context
                        }) {
                            Text(
                                "Sort",
                                color = Color.Black
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            filterDialogOpen = false
                            Toast.makeText(context, "Filter/Sort Applied", Toast.LENGTH_SHORT).show() // ใช้ context
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC2B)) // ปุ่มสีตามที่กำหนด
                    ) {
                        Text(
                            "Apply",
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { filterDialogOpen = false }) {
                        Text(
                            "Cancel",
                            color = Color.Black
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Show selected filters or sorts
        if (selectedFilter != null || selectedSort != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Selected Filters/Sort Options",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black
                    )
                )
                selectedFilter?.let {
                    Text(
                        text = "Filter: $it",
                        color = Color.Black
                    )
                }
                selectedSort?.let {
                    Text(
                        text = "Sort: $it",
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Room List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(roomList) { id ->
                RoomCard(id = id) // ส่ง id ของห้อง
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add Room Button
            item {
                AddRoomButton()
            }
        }
    }
}

@Composable
fun RoomCard(id: Int) {
    val context = LocalContext.current // เข้าถึง context ที่ใช้ในการแสดง Toast

    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                Toast.makeText(context, "Card ID: $id", Toast.LENGTH_SHORT).show() // ใช้ context
            }
            .background(Color(0xFFFFFFFF)), // เปลี่ยนเป็นสีเทาอ่อนสำหรับ Card
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = androidx.compose.material3.CardDefaults.elevatedCardElevation(8.dp) // ปรับให้มีเงา
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // จุดสีเขียวทางด้านซ้ายของรูป
            Box(
                modifier = Modifier
                    .size(10.dp) // ขนาดของจุด
                    .background(Color.Green, shape = RoundedCornerShape(50)) // รูปทรงวงกลมและสีเขียว
                    .align(Alignment.CenterVertically) // จัดให้อยู่กลางแนวตั้ง
            )

            // Room Info
            Image(
                painter = painterResource(R.drawable.logoapp),
                contentDescription = "Room Image",
                modifier = Modifier
                    .size(50.dp) // ขนาดของรูป
                    .padding(start = 8.dp) // ช่องว่างระหว่างจุดสีเขียวและรูป
                    .clip(RoundedCornerShape(8.dp)) // ขอบมุมรูปภาพ
            )

            // Room Info Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "สุนัข Deluxe",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "คำอธิบายห้องพัก",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ราคา: 1,200 บาท",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }

            // IconButton (More) on the right
            IconButton(
                onClick = {
                    Toast.makeText(context, "More", Toast.LENGTH_SHORT).show() // ใช้ context เพื่อแสดง Toast
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically) // จัดตำแหน่งให้อยู่กลางแนวตั้ง
                    .padding(start = 8.dp) // ช่องว่างระหว่างข้อมูลห้องและปุ่ม
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More",
                    tint = Color(0xFF3D3D3D) // ใช้สีเข้ม
                )
            }
        }}}

@Composable
fun AddRoomButton() {
    val context = LocalContext.current // เข้าถึง context

    Button(
        onClick = {
            Toast.makeText(context, "Add Room clicked", Toast.LENGTH_SHORT).show() // ใช้ context
        },
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC2B)) // ปุ่มสีตามที่กำหนด
    ) {
        Text(
            text = "เพิ่มห้องพัก",
            color = Color.White
        )
    }
}

