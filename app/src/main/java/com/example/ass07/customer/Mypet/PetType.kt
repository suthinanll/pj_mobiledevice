package com.example.ass07.customer.Mypet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class PetType(
    @Expose
    @SerializedName("pet_type_id") val Pet_type_id: Int,
    @Expose
    @SerializedName("pet_name_type") val Pet_name_type: String
)