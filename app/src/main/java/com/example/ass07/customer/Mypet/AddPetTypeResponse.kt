package com.example.ass07.customer.Mypet

data class AddPetTypeResponse(
    val error: Boolean,
    val message: String,
    val petType: PetType? // Make petType nullable
)
