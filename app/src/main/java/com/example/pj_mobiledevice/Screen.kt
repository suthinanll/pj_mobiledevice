package com.example.pj_mobiledevice

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

sealed class Screen(val route: String, val name: String, val icon: @Composable () -> Painter) {
    object Home : Screen(
        route = "home",
        name = "Home",
        icon = { painterResource(id = R.drawable.home) }
    )
    object History : Screen(
        route = "history",
        name = "History",
        icon = { painterResource(id = R.drawable.history) }
    )
    object MyPet : Screen(
        route = "mypet",
        name = "My Pet",
        icon = { painterResource(id = R.drawable.mypet) }
    )
    object Profile : Screen(
        route = "profile",
        name = "Profile",
        icon = { painterResource(id = R.drawable.user) }
    )
}

