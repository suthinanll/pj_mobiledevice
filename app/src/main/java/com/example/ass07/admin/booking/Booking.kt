package com.example.ass07.admin.booking

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(

    // ข้อมูลการจอง
    @Expose @SerializedName("booking_id") val bookingId: Int,
    @Expose @SerializedName("check_in") val checkIn: String,
    @Expose @SerializedName("check_out") val checkOut: String,
    @Expose @SerializedName("additional_info") val additionalInfo: String?,
    @Expose @SerializedName("pay") val pay: Int,
    @Expose @SerializedName("adjust") val adjust: Int?,
    @Expose @SerializedName("total_pay") val totalPay: Int,
    @Expose @SerializedName("booking_status") val status: Int,
    @Expose @SerializedName("payment_method") val paymentMethod: Int,
    @Expose @SerializedName("pet_id") val petId: Int,
    @Expose @SerializedName("room_id") val roomId: Int,
    @Expose @SerializedName("created_at") val createdAt: String,
    @Expose @SerializedName("updated_at") val updatedAt: String,
    @Expose @SerializedName("deleted_at") val deletedAt: String?,

    // ข้อมูลสัตว์เลี้ยง
    @Expose @SerializedName("pet_name") val petName: String,
    @Expose @SerializedName("pet_gender") val petGender: String,
    @Expose @SerializedName("pet_breed") val petBreed: String,
    @Expose @SerializedName("pet_age") val petAge: Int,
    @Expose @SerializedName("pet_height") val petHeight: Double,
    @Expose @SerializedName("pet_weight") val petWeight: Double,

    // ข้อมูลผู้ใช้
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("tell_number") val tellNumber: String,
    @Expose @SerializedName("email") val email: String,

    // ข้อมูลห้องพัก
    @Expose @SerializedName("type_type_id") val typeTypeId: Int,
    @Expose @SerializedName("status") val roomStatus: Int,

    // ข้อมูลประเภทห้อง
    @Expose @SerializedName("name_type") val roomType: String,
    @Expose @SerializedName("price_per_day") val pricePerDay: Int,
    @Expose @SerializedName("image") val roomImage: String?,
    @Expose @SerializedName("pet_type") val petType: Int,

    // ข้อมูลประเภทสัตว์ที่เข้าพัก
    @Expose @SerializedName("pet_name_type") val petNameType: String,

    // การชำระเงิน
    @Expose @SerializedName("method_name") val methodName: String

) : Parcelable


