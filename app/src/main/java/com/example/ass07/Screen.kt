package com.example.ass07

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

sealed class Screen(val route: String, val name: String, val icon: @Composable () -> Painter) {
    object Home : Screen(route = "Home", name = "Home", icon = { painterResource(id = R.drawable.home) })
    object History : Screen(route = "History", name = "History", { painterResource(id = R.drawable.history) })
    object MyPet : Screen(route = "MyPet", name = "MyPet", icon = { painterResource(id = R.drawable.mypet) })
    object Profile : Screen(route = "Profile", name = "Profile", { painterResource(id = R.drawable.user) })
}
