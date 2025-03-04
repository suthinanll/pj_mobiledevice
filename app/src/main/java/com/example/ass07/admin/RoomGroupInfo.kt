package com.example.ass07.admin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomGroupInfo(
    val roomType: String,
    val price: Double?,
    val petType: String,
    val availableCount: Int,
    val occupiedCount: Int,
    val improvedCount: Int,
    val image : String
):Parcelable