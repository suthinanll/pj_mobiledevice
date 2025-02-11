package com.example.ass07

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp


@Composable
fun ManageRoom() {
    val context = LocalContext.current
    var filterDialogOpen by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB)) // amber-50 equivalent
    ) {

        // Filter/Sort Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { filterDialogOpen = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6B7280)
                ),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logoapp),
                        contentDescription = "Filter",
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Filter")
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { /* Handle sort */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6B7280)
                ),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.logoapp),
                        contentDescription = "Sort",
                        modifier = Modifier.size(18.dp)
                    )
                    Text("Sort")
                }
            }
        }

        // Room List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(getSampleRooms()) { room ->
                RoomCard(room = room)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                AddRoomButton()
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Filter Dialog
    if (filterDialogOpen) {
        AlertDialog(
            onDismissRequest = { filterDialogOpen = false },
            title = {
                Text(
                    "Filter Options",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterOption("Available Rooms") {
                        selectedFilter = "available"
                        filterDialogOpen = false
                    }
                    FilterOption("Occupied Rooms") {
                        selectedFilter = "occupied"
                        filterDialogOpen = false
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { filterDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFBBF24) // amber-400
                    )
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { filterDialogOpen = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RoomCard(room: Room) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { /* Handle click */ },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (room.isAvailable) Color(0xFF22C55E) // green-500
                        else Color(0xFFFBBF24), // amber-400
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Room image
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFFDE68A), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoapp),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Room details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = room.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "฿${room.price}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFD97706) // amber-600
                )
            }

            IconButton(onClick = { /* Handle more options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color(0xFF9CA3AF) // gray-400
                )
            }
        }
    }
}

@Composable
fun AddRoomButton() {
    Button(
        onClick = { /* Handle add room */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFBBF24) // amber-400
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add room",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("เพิ่มห้องพัก")
        }
    }
}

@Composable
fun FilterOption(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color(0xFF6B7280)
        )
    ) {
        Text(text)
    }
}

// Data class for room
data class Room(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val isAvailable: Boolean
)

// Sample data
fun getSampleRooms() = listOf(
    Room(1, "สุนัข Deluxe", "ห้องพักสุดหรูสำหรับสุนัข", 1200.0, true),
    Room(2, "แมว Premium", "ห้องพักแสนสบายสำหรับแมว", 900.0, false)
)