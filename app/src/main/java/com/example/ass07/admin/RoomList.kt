//package com.example.ass07.admin
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import com.example.ass07.RoomCard
//
//@Composable
//fun RoomDetailsScreen(
//    rooms: List<Room>,
//    roomType: String,
//    price: Double,
//    petType: String
//) {
//    val filteredRooms = remember(roomType, price, petType) {
//        rooms.filter { room ->
//            room.room_type == roomType &&
//                    room.price_per_day == price &&
//                    room.pet_type == petType
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFFFBEB))
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "ห้องที่ตรงกับเงื่อนไข",
//            style = MaterialTheme.typography.headlineSmall,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            items(filteredRooms) { room ->
//                RoomCard(room = room)
//            }
//        }
//    }
//}