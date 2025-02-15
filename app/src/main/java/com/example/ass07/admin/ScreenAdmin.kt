package com.example.ass07.admin

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.ass07.R

sealed class ScreenAdmin(val route: String, val name: String, val icon: (@Composable () -> Painter)) {
    object ManageRoom : ScreenAdmin(route = "ManageRoom", name = "Manage Room", icon = { painterResource(id = R.drawable.home) })
    object Booking : ScreenAdmin(route = "Booking", name = "Room Reservation", icon = { painterResource(id = R.drawable.history) })
    object PetsAdmin : ScreenAdmin(route = "PetsAdmin", name = "Pets", icon = { painterResource(id = R.drawable.mypet) })


}
