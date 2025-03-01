package com.example.ass07.admin


import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Room(
    @Expose
    @SerializedName("room_id") val room_id: Int,
    @Expose
    @SerializedName("room_type") val room_type: String,
    @Expose
    @SerializedName("price_per_day") val price_per_day: Double?,
    @Expose
    @SerializedName("room_status") val room_status: Int,
    @Expose
    @SerializedName("type_type_id") val type_type_id: Int,
    @Expose
    @SerializedName("name_type") val name_type: String,
    @Expose
    @SerializedName("type_id") val room_type_id: Int,
    @Expose
    @SerializedName("pet_type") val pet_type: String,
    @Expose
    @SerializedName("pet_type_name") val pet_type_name: String,
    @Expose
    @SerializedName("image") val image: String,
    @Expose
    @SerializedName("user_id") val userId: Int,
    @Expose
    @SerializedName("pet_id") val petId: Int,
) : Parcelable

data class RoomStatistic(
    val type: String,
    val available: Int,
    val booked: Int,
    val clean: Int
)


data class Booking(
    val bookingId: Int,
    val name: String,
    val petName: String,
    val tellNumber: String,
    val checkIn: String,
    val checkOut: String,
    val status: Int,
    val createdAt: String
)

