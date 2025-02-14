package com.example.ass07.customer.LoginRegister

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.ass07.R

sealed class ScreenLogin(val route : String , val name : String){
    data object Login:ScreenLogin("login_screen","Login")
    data object Register:ScreenLogin("register_screen","Register")

}