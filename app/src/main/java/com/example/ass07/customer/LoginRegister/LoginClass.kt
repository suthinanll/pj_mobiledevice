package com.example.ass07.customer.LoginRegister

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginClass(
    @Expose
    @SerializedName("success") val success : Int,

    @Expose
    @SerializedName("name") val name : String,

    @Expose
    @SerializedName("user_type") val user_type : Int,

    @Expose
    @SerializedName("user_id") val user_id: Int,

    @Expose
    @SerializedName("email") val email: String,

    @Expose
    @SerializedName("tell_number") val tell_number: String
): Parcelable {}
