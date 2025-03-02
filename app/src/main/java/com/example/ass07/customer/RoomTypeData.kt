package com.example.ass07.customer

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class RoomTypeData(
    @Expose
    @SerializedName("type_id") val typeId : Int,

    @Expose
    @SerializedName("name_type") val nameType : String,

    @Expose
    @SerializedName("price_per_day") val pricePerDay : Int,

    @Expose
    @SerializedName("image") val image : String,

    @Expose
    @SerializedName("pet_type") val petType : Int,

    @Expose
    @SerializedName("created_at") val createdAt : Timestamp,

    @Expose
    @SerializedName("updated_at") val updatedAt : Timestamp,

    @Expose
    @SerializedName("deleted_at") val deletedAt : Timestamp
) : Parcelable
