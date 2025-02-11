package com.example.ass07

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Member(
    @Expose
    @SerializedName("name") val user_name: String,

    @Expose
    @SerializedName("tell_number") val tell: String,

    @Expose
    @SerializedName("email") val email: String,

    )

