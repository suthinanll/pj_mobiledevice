package com.example.ass07

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

sealed class Screen(val route: String, val name: String, val icon: (@Composable () -> Painter)) {
    object Home : Screen(route = "Home", name = "Home", icon = { painterResource(id = R.drawable.home) })
    object History : Screen(route = "History", name = "History", icon = { painterResource(id = R.drawable.history) })
    object MyPet : Screen(route = "MyPet", name = "MyPet", icon = { painterResource(id = R.drawable.mypet) })
    object Profile : Screen(route = "Profile", name = "Profile", icon = { painterResource(id = R.drawable.user) })
    object Dashboard : Screen("dashboard", name = "Dashboard", icon = { painterResource(id = R.drawable.user) })
    object RoomManagement : Screen("room_manage", name = "Room Management", icon = { painterResource(id = R.drawable.user) })
    object Mypetinsert : Screen(
        route = "Mypetinsert",
        name = "Mypetinsert",
        icon = {
            remember {
                object : Painter() {
                    override val intrinsicSize = Size(1f, 1f)
                    override fun DrawScope.onDraw() { /* ไม่วาดอะไร */ }
                }
            }
        }
    )
    object Mypetedit : Screen(
        route = "Mypetedit",
        name = "Mypetedit",
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

