package com.example.ass07.customer

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class PetData(
    @Expose
    @SerializedName("pet_id") val petId : Int,

    @Expose
    @SerializedName("pet_name") val petName : String,

    @Expose
    @SerializedName("pet_gender") val petGender : String,

    @Expose
    @SerializedName("user_id") val userId : Int,

    @Expose
    @SerializedName("pet_type_id") val petTypeId : Int,

    @Expose
    @SerializedName("pet_breed") val petBreed : String,

    @Expose
    @SerializedName("pet_age") val patAge : Int,

    @Expose
    @SerializedName("pet_height") val petHeight : Double,

    @Expose
    @SerializedName("pet_weight") val petWeight : Double,

    @Expose
    @SerializedName("additional_info") val additionalInfo : String,

    @Expose
    @SerializedName("created_at") val createdAt : Timestamp,

    @Expose
    @SerializedName("updated_at") val updatedAt : Timestamp,

    @Expose
    @SerializedName("deleted_at") val deletedAt : Timestamp,
) : Parcelable
