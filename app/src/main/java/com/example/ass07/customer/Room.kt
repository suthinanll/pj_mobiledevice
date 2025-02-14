package com.example.ass07.customer


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    @Expose
    @SerializedName("room_id") val room_id: Int,
    @Expose
    @SerializedName("room_type") val room_type :String,
    @Expose
    @SerializedName("price_per_day") val price_per_day:String,
    @Expose
    @SerializedName("status") val room_status:Int,
    @Expose
    @SerializedName("type_type_id") val emp_salary:Int,
    @Expose
    @SerializedName("name_type") val name_type:String,
    @Expose
    @SerializedName("type_id") val type_id:Int,
    @Expose
    @SerializedName("pet_type") val pet_type:Int,
    @Expose
    @SerializedName("image") val image:String,
    @Expose
    @SerializedName("user_id") val userId: Int
): Parcelable {}


