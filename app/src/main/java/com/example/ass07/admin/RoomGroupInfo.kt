package com.example.ass07.admin

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoomGroupInfo(
    val roomType: String,
    val price: Int,
    val petType: String,
    val availableCount: Int,
    val occupiedCount: Int
):Parcelable