package com.example.ass07

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PetType(
    @Expose
    @SerializedName("Pet_type_id") val Pet_type_id: Int,
    @Expose
    @SerializedName("Pet_nametype") val Pet_name_type: String // ✅ ใช้ `Pet_nametype` ตาม API
)

