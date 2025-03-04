package com.example.ass07.customer

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class RoomData(
    @Expose
    @SerializedName("room_id") val roomId : Int,

    @Expose
    @SerializedName("type_type_id") val typeTypeId : Int,

    @Expose
    @SerializedName("status") val status : Int,

    @Expose
    @SerializedName("created_at") val createdAt : Timestamp,

    @Expose
    @SerializedName("updated_at") val updatedAt : Timestamp,

    @Expose
    @SerializedName("deleted_at") val deletedAt : Timestamp,
) : Parcelable
