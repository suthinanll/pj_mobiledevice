package com.example.ass07.customer.Mypet

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class petMember (
    @Expose
    @SerializedName("pet_id") val petID: String,

    @Expose
    @SerializedName("pet_name") val petName: String,

    @Expose
    @SerializedName("pet_gender") val petGender: String,

    @Expose
    @SerializedName("pet_breed") val petBreed: String,

    @Expose
    @SerializedName("pet_age") val petAge: Int,

    @Expose
    @SerializedName("pet_weight") val petWeight: Double,

    @Expose
    @SerializedName("additional_info") val additionalInfo: String,

    @Expose
    @SerializedName("pet_name_type") val petTypename: String,

    @Expose
    @SerializedName("pet_type_id") val Pet_type_id: Int,

    @Expose
    @SerializedName("deleted_at") val deleted_at: String?,

    @Expose
    @SerializedName("user_id") val userId: Int
)
