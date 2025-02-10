package com.example.ass07

import android.hardware.Camera
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

sealed class ScreenAdmin(val route: String, val name: String, val icon: (@Composable () -> Painter)) {
    object ManageRoom : ScreenAdmin(route = "ManageRoom", name = "Manage Room", icon = { painterResource(id = R.drawable.home) })
    object Booking : ScreenAdmin(route = "Booking", name = "Room Reservation", icon = { painterResource(id = R.drawable.history) })
    object PetsAdmin : ScreenAdmin(route = "PetsAdmin", name = "Pets", icon = { painterResource(id = R.drawable.mypet) })

}
