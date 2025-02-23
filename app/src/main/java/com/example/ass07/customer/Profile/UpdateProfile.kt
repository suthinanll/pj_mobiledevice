package com.example.ass07.customer.Profile

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val tell_number: String,
    val avatar: Int
)

data class UpdateProfileResponse(
    val message: String
)
