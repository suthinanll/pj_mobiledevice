package com.example.ass07.admin

data class RoomTypeResponse(
    val error: Boolean,
    val message: String,
    val roomType: RoomType? = null
)