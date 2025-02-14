package com.example.ass07

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class petMember (
    @Expose
    @SerializedName("Pet_id") val petID: String,

    @Expose
    @SerializedName("Pet_name") val petName: String,

    @Expose
    @SerializedName("Pet_Gender") val petGender: String,

    @Expose
    @SerializedName("Pet_breed") val petBreed: String,

    @Expose
    @SerializedName("Pet_age") val petAge: Int,

    @Expose
    @SerializedName("Pet_weight") val petWeight: Int,

    @Expose
    @SerializedName("additional_info") val additionalInfo: String,

    @Expose
    @SerializedName("Pet_nametype") val petTypename: String,

    @Expose
    @SerializedName("Pet_type_id") val Pet_type_id: Int,

    @Expose
    @SerializedName("deleted_at") val deleted_at: String?,

    @Expose
    @SerializedName("user_id") val userId: Int
)