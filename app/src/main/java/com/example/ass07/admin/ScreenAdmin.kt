package com.example.ass07.admin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.ass07.R
import com.example.ass07.customer.Screen

sealed class ScreenAdmin(val route: String, val name: String, val icon: (@Composable () -> Painter)) {
    object ManageRoom : ScreenAdmin(route = "ManageRoom", name = "Manage Room", icon = { painterResource(id = R.drawable.home) })
    object Booking : ScreenAdmin(route = "Booking", name = "Room Reservation", icon = { painterResource(id = R.drawable.history) })
    object PetsAdmin : ScreenAdmin(route = "PetsAdmin", name = "Pets", icon = { painterResource(id = R.drawable.mypet) })
    object RoomInsert : ScreenAdmin(
        route = "RoomInsert",
        name = "RoomInsert",
        icon = {
            remember {
                object : Painter() {
                    override val intrinsicSize = Size(1f, 1f)
                    override fun DrawScope.onDraw() { /* ไม่วาดอะไร */ }
                }
            }
        }
    )

    object BookingDetail : ScreenAdmin(
        route = "BookingDetail",
        name = "BookingDetail",
        icon = {
            remember {
                object : Painter() {
                    override val intrinsicSize = Size(1f, 1f)
                    override fun DrawScope.onDraw() { /* ไม่วาดอะไร */ }
                }
            }
        }
    )

}
