package com.example.ass07.customer

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @Expose @SerializedName("user_id") val userId: Int,
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("tell_number") val tellNumber: String,
    @Expose @SerializedName("email") val email: String,
    @Expose @SerializedName("user_type") val userType: Int,
    @Expose @SerializedName("password") val password: String,
    @Expose @SerializedName("avatar") val avatar: Int
):Parcelable

