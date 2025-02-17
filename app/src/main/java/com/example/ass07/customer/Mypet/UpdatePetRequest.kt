package com.example.ass07.customer.Mypet


data class UpdatePetRequest(
    val pet_name: String,
    val pet_gender: String,
    val pet_breed: String,
    val pet_age: Int,
    val pet_weight: Double,
    val additional_info: String,
    val pet_type_id: Int
)