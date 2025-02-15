package com.example.ass07.admin

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(
    @Expose
    @SerializedName("booking_id") val bookingId: Int,

    @Expose
    @SerializedName("check_in") val checkIn: String,

    @Expose
    @SerializedName("check_out") val checkOut: String,

    @Expose
    @SerializedName("additional_info") val additionalInfo: String?, // อาจเป็น null ได้

    @Expose
    @SerializedName("pay") val pay: Int,

    @Expose
    @SerializedName("adjust") val adjust: Int?, // อาจเป็น null ได้

    @Expose
    @SerializedName("total_pay") val totalPay: Int,

    @Expose
    @SerializedName("payment_method") val paymentMethod: Int,

    @Expose
    @SerializedName("pet_id") val petId: Int,

    @Expose
    @SerializedName("room_id") val roomId: Int,

    @Expose
    @SerializedName("created_at") val createdAt: String,

    @Expose
    @SerializedName("updated_at") val updatedAt: String,

    @Expose
    @SerializedName("deleted_at") val deletedAt: String? // Soft Delete ให้รับค่า null ได้
) : Parcelable

