package com.example.ass07.admin.booking

data class ExtendBookingRequest(
    val days: Int,
    val additionalCost: Int
)

data class ExtendBookingResponse(
    val message: String,
    val data: ExtendBookingData
)

data class ExtendBookingData(
    val bookingId: Int,
    val newCheckOut: String,
    val additionalCost: Int,
    val newTotalPay: Int
)
