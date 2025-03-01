package com.example.ass07.admin

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomType(
    @Expose
    @SerializedName("type_id") val room_type_id: Int, // Correct the naming here to match with room_type_id
    @Expose
    @SerializedName("name_type") val name_type: String,
    @Expose
    @SerializedName("price_per_day") val price_per_day: Double?,
    @Expose
    @SerializedName("image") val image: String?,
    @Expose
    @SerializedName("pet_type") val pet_type: String? // Use pet_type as declared in the API response
) : Parcelable

