package com.example.ass07.customer

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class PaymentMethodData(
    @Expose
    @SerializedName("method_id") val methodId : Int,

    @Expose
    @SerializedName("method_name") val methodName : String,

    @Expose
    @SerializedName("created_at") val createdAt : Timestamp,

    @Expose
    @SerializedName("updated_at") val updatedAt : Timestamp,

    @Expose
    @SerializedName("deleted_at") val deletedAt : Timestamp
) : Parcelable
