package com.example.ass07

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import coil.compose.AsyncImage
import com.example.ass07.admin.RoomAPI
import com.example.ass07.admin.RoomType
import com.example.ass07.admin.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// RoomUIState.kt
sealed class RoomUIState {
    object Loading : RoomUIState()
    data class Success(val rooms: List<Room>) : RoomUIState()
    data class Error(val message: String) : RoomUIState()
}

sealed class RoomTypeUIState {
    object Loading : RoomTypeUIState()
    data class Success(val roomTypes: List<RoomType>) : RoomTypeUIState()
    data class Error(val message: String) : RoomTypeUIState()
}
// ManageRoom.kt
@Composable
fun ManageRoom() {
    val scope = rememberCoroutineScope()
    var uiState by remember { mutableStateOf<RoomUIState>(RoomUIState.Loading) }
    var filterDialogOpen by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current



    LaunchedEffect(true) {
        scope.launch {
            try {
                val api = RoomAPI.create()
                val response = withContext(Dispatchers.IO) {
                    api.retrieveAllRooms().execute()
                }
                uiState = if (response.isSuccessful) {
                    RoomUIState.Success(response.body() ?: emptyList())
                } else {
                    RoomUIState.Error("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                uiState = RoomUIState.Error("Error: ${e.message}")
            }
        }
    }

//    LaunchedEffect(Unit) { // Or LaunchedEffect(true) is fine too.
//        scope.launch {
//            try {
//                val api = RoomAPI.create()
//                val response = withContext(Dispatchers.IO) {
//                    api.getRoomTypes().execute() // Call the new API method
//                }
//                RoomTypeUIState = if (response.isSuccessful) {
//                    RoomTypeUIState.Success(response.body() ?: emptyList())
//                } else {
//                    val errorMessage = "Error: ${response.code()} - ${response.message()}"
//                    RoomTypeUIState.Error(errorMessage)
//                }
//            } catch (e: Exception) {
//                RoomTypeUIState = RoomTypeUIState.Error("Error: ${e.message}")
//            }
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBEB))
    ) {
        // Top Bar with Filter and Sort
        TopActionBar(
            onFilterClick = { filterDialogOpen = true },
            onSortClick = { /* Handle sort */ }
        )

        // Main Content
        when (val currentState = uiState) {
            is RoomUIState.Loading -> LoadingScreen()
            is RoomUIState.Success -> RoomList(
                rooms = currentState.rooms,
                roomTypeUiState = TODO()
            )
            is RoomUIState.Error -> ErrorScreen(message = currentState.message)
            else -> {}
        }
    }

    // Filter Dialog
    if (filterDialogOpen) {
        FilterDialog(
            onDismiss = { filterDialogOpen = false },
            onFilterSelect = { filter ->
                selectedFilter = filter
                filterDialogOpen = false
            }
        )
    }
}

@Composable
fun TopActionBar(
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { /* Handle click */ }
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,  // ไอคอนสามขีด
                contentDescription = "More options", // คำอธิบายสำหรับผู้ใช้ที่ใช้เครื่องมือช่วย
                modifier = Modifier.size(32.dp), // ขนาดของไอคอน
                tint = Color.Black // สีของไอคอน
            )
        }

        // ข้อความ "Filter"
        Text(
            text = "Filter",
            modifier = Modifier
                .align(Alignment.CenterVertically) // จัดแนวข้อความแนวตั้งให้ตรงกลาง
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = { /* Handle click */ }
        ) {
            Icon(
                imageVector = Icons.Default.Search,  // เปลี่ยนเป็นไอคอน Search
                contentDescription = "Search", // คำอธิบายสำหรับผู้ใช้ที่ใช้เครื่องมือช่วย
                modifier = Modifier.size(32.dp), // ขนาดของไอคอน
                tint = Color.Black // สีของไอคอน
            )
        }

        // ข้อความ "Filter"
        Text(
            text = "Filter",
            modifier = Modifier
                .align(Alignment.CenterVertically) // จัดแนวข้อความแนวตั้งให้ตรงกลาง
        )
    }
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    text: String
) {
    Button(
        onClick = onClick,
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
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(18.dp)
            )
            Text(text)
        }
    }
}

@Composable
fun RoomList(rooms: List<Room>, roomTypeUiState: RoomTypeUIState) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = rooms,
            key = { it.room_id }
        ) { room ->
            key(room.room_id) {
                RoomCard(room = room)
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            AddRoomButton(roomTypeUiState)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RoomCard(room: Room) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = { /* Handle click */ }
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val statusColor = remember(room.room_status) {
                if (room.room_status == 1) Color(0xFF22C55E) else Color(0xFFFBBF24)
            }

            // Status indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(statusColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Room image
            AsyncImage(
                model = R.drawable.logoapp,
                contentDescription = "Room Logo Image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Room details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = room.room_type,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = "฿${room.price_per_day}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFFD97706)
                )
            }

            // More options menu
            var showMenu by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color(0xFF9CA3AF)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = { /* Handle edit */ }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { /* Handle delete */ }
                    )
                }
            }
        }
    }
}
@Composable
fun AddRoomButton(roomTypeUiState: RoomTypeUIState) { // Pass in the state

    var selectedRoomId by remember { mutableStateOf<Int?>(null) } // Store the ID, not the whole Pair
    var expanded by remember { mutableStateOf(false) }
    var responseMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Button(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                // Display the selected room type name, handling loading/error states
                val buttonText = when (roomTypeUiState) {
                    is RoomTypeUIState.Loading -> "Loading room types..."
                    is RoomTypeUIState.Success -> {
                        val selectedRoomType = roomTypeUiState.roomTypes.find { it.type_id == selectedRoomId }
                        selectedRoomType?.name_type ?: "Select Room Type" // Default text if none selected
                    }
                    is RoomTypeUIState.Error -> "Error loading room types"
                    else -> {}
                }
                Text("เลือกประเภทห้อง: $buttonText")
            }

            // Dropdown menu: Handle loading, success, and error states.
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                when (roomTypeUiState) {
                    is RoomTypeUIState.Loading -> {
                        DropdownMenuItem(text = { Text("Loading...") }, onClick = {})
                    }
                    is RoomTypeUIState.Success -> {
                        roomTypeUiState.roomTypes.forEach { roomType ->
                            DropdownMenuItem(
                                text = { Text(roomType.name_type) },
                                onClick = {
                                    selectedRoomId = roomType.type_id
                                    expanded = false
                                }
                            )
                        }
                    }
                    is RoomTypeUIState.Error -> {
                        DropdownMenuItem(text = { Text("Error") }, onClick = {})
                    }

                    else -> {}
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Use selectedRoomId directly (it's an Int now)
                val api = RoomAPI.create()
                if (selectedRoomId != null) { // Make sure a room type is selected.
                    api.insertRoom(
                        typeTypeId = selectedRoomId!!,
                        status = 1
                    ).enqueue(object : Callback<Room> {
                        @OptIn(UnstableApi::class)
                        override fun onResponse(call: Call<Room>, response: Response<Room>) {
                            responseMessage = if (response.isSuccessful) {
                                "เพิ่มห้องพักสำเร็จ: ${response.body()?.room_type}"
                            } else ({
                                val errorBody = response.errorBody()?.string()
                                responseMessage =
                                    "เกิดข้อผิดพลาด: ${response.code()} - ${response.message()} - $errorBody"
                                Log.e(
                                    "AddRoomButton",
                                    "Error adding room: ${response.code()} - ${response.message()} - $errorBody"
                                )
                            }).toString()
                        }

                        @OptIn(UnstableApi::class)
                        override fun onFailure(call: Call<Room>, t: Throwable) {
                            responseMessage = "การเชื่อมต่อล้มเหลว: ${t.message}"
                            Log.e("AddRoomButton", "Network Failure", t)
                        }
                    })
                } else {
                    responseMessage = "กรุณาเลือกประเภทห้อง" // Error message when room has not selected.
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24)),
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

        if (responseMessage.isNotEmpty()) {
            Text(
                text = responseMessage,
                modifier = Modifier.padding(top = 8.dp),
                color = if (responseMessage.startsWith("เพิ่มห้องพักสำเร็จ")) Color.Black else Color.Red
            )
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = Color.Red)
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onFilterSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Options", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterOption("Available Rooms") {
                    onFilterSelect("available")
                    onDismiss()
                }
                FilterOption("Occupied Rooms") {
                    onFilterSelect("occupied")
                    onDismiss()
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBBF24))
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FilterOption(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF6B7280))
    ) {
        Text(text)
    }
}