package com.example.ass07.customer.Home


import com.example.ass07.admin.Room

data class AvailableRoomsResponse(
    val error: Boolean,
    val message: String,
    val check_in: String,
    val check_out: String,
    val available_rooms: List<Room>
)