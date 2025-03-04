package com.example.ass07.customer

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class BookingData(
    @Expose
    @SerializedName("booking_id") val bookingId : Int,

    @Expose
    @SerializedName("check_in") val checkIn : String,

    @Expose
    @SerializedName("check_out") val checkOut : String,

    @Expose
    @SerializedName("additional_info") val additionalInfo : String,

    @Expose
    @SerializedName("pay") val pay : Int,

    @Expose
    @SerializedName("adjust") val adjust : Int,

    @Expose
    @SerializedName("total_pay") val totalPay : Int,

    @Expose
    @SerializedName("booking_status") val bookingStatus : Int,

    @Expose
    @SerializedName("payment_method") val paymentMethod : Int,

    @Expose
    @SerializedName("pet_id") val petId : Int,

    @Expose
    @SerializedName("room_id") val roomId : Int,

    @Expose
    @SerializedName("created_at") val createdAt : Timestamp,

    @Expose
    @SerializedName("updated_at") val updatedAt : Timestamp,

    @Expose
    @SerializedName("deleted_at") val deletedAt : Timestamp
) : Parcelable
