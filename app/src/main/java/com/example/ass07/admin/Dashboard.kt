package com.example.ass07.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.example.ass07.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AdminDashboard() {
    val context = LocalContext.current
    val roomAPI = remember { RoomAPI.create() }
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var rooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var totalPets by remember { mutableStateOf(0) }
    var availableRooms by remember { mutableStateOf(0) }
    var roomStatistics by remember { mutableStateOf<List<RoomStatistic>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                val response = roomAPI.retrieveAllRooms().execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val roomsList = response.body()
                        if (roomsList != null) {
                            rooms = roomsList

                            Log.d("AdminDashboard", "Retrieved ${rooms.size} rooms")
                            rooms.forEach { room ->
                                Log.d("RoomDetails", """
                                    Room ID: ${room.room_id}
                                    Room Type: ${room.room_type}
                                    Pet Type: ${room.pet_type}
                                    Status: ${room.room_status}
                                    Price: ${room.price_per_day}
                                """.trimIndent())
                            }

                            totalPets = rooms.count { it.room_status == 1 }
                            availableRooms = rooms.count { it.room_status == 0 }

                            Log.d("Statistics", "Total Pets: $totalPets")
                            Log.d("Statistics", "Available Rooms: $availableRooms")

                            roomStatistics = rooms
                                .groupBy { it.pet_type }
                                .map { (petType, roomsForType) ->
                                    RoomStatistic(
                                        type = petType,
                                        available = roomsForType.count { it.room_status == 0 },
                                        booked = roomsForType.count { it.room_status == 1 },
                                        clean = roomsForType.count{it.room_status == 2}
                                    )
                                }

                            roomStatistics.forEach { stat ->
                                Log.d("RoomStatistics", """
                                    Pet Type: ${stat.type}
                                    Available: ${stat.available}
                                    Booked: ${stat.booked}
                                """.trimIndent())
                            }
                        } else {
                            error = "Response body is null"
                        }
                    } else {
                        error = "Error: ${response.code()} - ${response.message()}"
                        Log.e("AdminDashboard", "API Error: $error")
                    }
                    isLoading = false
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                error = e.message
                Log.e("AdminDashboard", "Exception: ${e.message}", e)
                isLoading = false
            }
        }
    }

    // UI Components
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (error != null) {
            Text("Error: $error", color = Color.Red)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ยินดีต้อนรับ",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "แดชบอร์ดผู้ดูแลระบบ",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        Toast.makeText(context, "การตั้งค่า", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "ตั้งค่า",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // สรุปภาพรวม
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                painterResource(id = R.drawable.animals),
                                title = "สัตว์เลี้ยงทั้งหมด",
                                value = "$totalPets ตัว"
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                painterResource(id = R.drawable.open_door),
                                title = "ห้องว่าง",
                                value = "$availableRooms ห้อง"
                            )
                        }
                    }

                    // ห้องพักแยกตามประเภท
                    item {
                        DashboardCard(
                            title = "สถานะห้องพัก",
                            icon = Icons.Default.Home
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                roomStatistics.forEach { stat ->
                                    RoomTypeRow(
                                        type = stat.type,
                                        available = "${stat.available} ว่าง",
                                        booked = "${stat.booked} จอง",
                                        clean = "${stat.clean} ทำความสะอาด"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class RoomStatistic(
    val type: String,
    val available: Int,
    val booked: Int,
    val clean:Int
)

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            content()
        }
    }
}

@Composable
fun RoomTypeRow(
    type: String,
    available: String,
    booked: String,
    clean:String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(type, style = MaterialTheme.typography.bodyLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                available,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                booked,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                clean,
                color = Color.Yellow
            )
        }
    }
}

@Composable
fun OccupancyItem(
    period: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = period,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text)
        }
    }
}
