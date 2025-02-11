package com.example.ass07

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class users(
    @Expose
    @SerializedName("name") val name: String,


    @Expose
    @SerializedName("tell_number") val tell_number: Int,


    @Expose
    @SerializedName("email") val email: String,

    @Expose
    @SerializedName("password") val password: Int,
)
