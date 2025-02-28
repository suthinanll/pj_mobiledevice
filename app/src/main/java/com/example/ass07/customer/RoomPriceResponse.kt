package com.example.ass07.customer

data class RoomPriceResponse(
    val room_id: Int,
    val days: Int,
    val payPerNight: Double,
    val totalPrice: Double

)
