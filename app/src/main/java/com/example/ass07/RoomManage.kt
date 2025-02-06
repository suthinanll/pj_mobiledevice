package com.example.ass07

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val customFontFamily = FontFamily(
    Font(R.font.thai), // ฟอนต์ปกติ
    Font(R.font.thai, FontWeight.Bold) // ฟอนต์ที่มีน้ำหนัก Bold
)
@Composable
fun RoomManagementScreen(onToast: (String) -> Unit) {
    var filterDialogOpen by remember { mutableStateOf(false) }

    // Storing selected options
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var selectedSort by remember { mutableStateOf<String?>(null) }

    val roomList = List(10) { it } // Example room list


    Column(modifier = Modifier.fillMaxSize().padding(16.dp).background(Color.Gray.copy(alpha = 0.5f))) {
        // Header with Title and Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onToast("Back clicked") }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Text(
                text = "Room Management",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.Black,
                    fontFamily = customFontFamily
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter/Sort Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { filterDialogOpen = true }) {
                Text(
                    text = "Filter/Sort",
                    color = Color.Black, // เปลี่ยนเป็นสีดำ
                    fontFamily = customFontFamily
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
                        color = Color.Black, // เปลี่ยนเป็นสีดำ
                        fontFamily = customFontFamily
                    )
                },
                text = {
                    Column {
                        TextButton(onClick = {
                            selectedFilter = "Filter Option"
                            filterDialogOpen = false
                            onToast("Filter selected")
                        }) {
                            Text(
                                "Filter",
                                color = Color.Black, // เปลี่ยนเป็นสีดำ
                                fontFamily = customFontFamily
                            )
                        }
                        TextButton(onClick = {
                            selectedSort = "Sort Option"
                            filterDialogOpen = false
                            onToast("Sort selected")
                        }) {
                            Text(
                                "Sort",
                                color = Color.Black, // เปลี่ยนเป็นสีดำ
                                fontFamily = customFontFamily
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            filterDialogOpen = false
                            onToast("Filter/Sort Applied")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black) // เปลี่ยนเป็นสีดำ
                    ) {
                        Text(
                            "Apply",
                            color = Color.White, // สีขาวเพื่อให้เห็นชัด
                            fontFamily = customFontFamily
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { filterDialogOpen = false }) {
                        Text(
                            "Cancel",
                            color = Color.Black, // เปลี่ยนเป็นสีดำ
                            fontFamily = customFontFamily
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
                        color = Color.Black, // เปลี่ยนเป็นสีดำ
                        fontFamily = customFontFamily
                    )
                )
                selectedFilter?.let {
                    Text(
                        text = "Filter: $it",
                        color = Color.Black, // เปลี่ยนเป็นสีดำ
                        fontFamily = customFontFamily
                    )
                }
                selectedSort?.let {
                    Text(
                        text = "Sort: $it",
                        color = Color.Black, // เปลี่ยนเป็นสีดำ
                        fontFamily = customFontFamily
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Room List
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(roomList) { id ->
                RoomCard(id = id, onToast = onToast)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add Room Button
            item {
                AddRoomButton(onToast)
            }
        }
    }
}

@Composable
fun RoomCard(id: Int, onToast: (String) -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToast("Card ID: $id") }
            .background(Color.Gray),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = androidx.compose.material3.CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Status Indicator
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.Green) // Replace with dynamic color
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Room Preview Image
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Room Preview",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Room Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "สุนัข Deluxe",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black, // เปลี่ยนเป็นสีดำ
                    fontFamily = customFontFamily
                )
                Text(
                    text = "คำอธิบายห้องพัก",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = customFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ราคา: 1,200 บาท",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black, // เปลี่ยนเป็นสีดำ
                    fontFamily = customFontFamily
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // More Button using Icons.Default.MoreVert
            IconButton(onClick = { onToast("More for ID: $id") }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun AddRoomButton(onToast: (String) -> Unit) {
    Button(
        onClick = { onToast("Add Room clicked") },
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black) // เปลี่ยนเป็นสีดำ
    ) {
        Text(
            text = "เพิ่มห้องพัก",
            color = Color.White, // สีขาวเพื่อให้เห็นชัด
            fontFamily = customFontFamily
        )
    }
}
