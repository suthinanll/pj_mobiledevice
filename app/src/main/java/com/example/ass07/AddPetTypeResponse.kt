package com.example.ass07

import com.example.ass07.customer.Mypet.PetType

data class AddPetTypeResponse(
    val error: Boolean,
    val message: String,
    val petType: PetType? // Make petType nullable
)