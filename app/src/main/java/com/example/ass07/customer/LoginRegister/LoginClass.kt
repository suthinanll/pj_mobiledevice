package com.example.ass07.customer.LoginRegister

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginClass(
    @Expose
    @SerializedName("success") val success : Int,

    @Expose
    @SerializedName("name") val name : String,

    @Expose
    @SerializedName("user_type") val user_type : Int
)
