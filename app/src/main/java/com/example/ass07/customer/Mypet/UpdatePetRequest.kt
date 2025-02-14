package com.example.ass07.customer.Mypet

data class UpdatePetRequest(
    val petName: String,
    val petGender: String,
    val petBreed: String,
    val petAge: Int,
    val petWeight: Int,
    val additionalInfo: String,
    val Pet_type_id: Int
)
