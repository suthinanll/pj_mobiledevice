package com.example.ass07.customer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.ass07.R
import com.example.ass07.customer.LoginRegister.ScreenLogin

sealed class Screen(val route: String, val name: String, val icon: (@Composable () -> Painter)) {
    object Home : Screen(route = "Home", name = "Home", icon = { painterResource(id = R.drawable.home) })
    object History : Screen(route = "History", name = "History", icon = { painterResource(id = R.drawable.history) })
    object MyPet : Screen(route = "MyPet", name = "MyPet", icon = { painterResource(id = R.drawable.mypet) })
    object Profile : Screen(route = "Profile", name = "Profile", icon = { painterResource(id = R.drawable.user) })


    object BookingInfo : Screen(route = "BookingInfo", name = "BookingInfo", icon = { painterResource(id = R.drawable.user) })
    object Payment : Screen(route = "payment_screen/{checkIn}/{checkOut}/{totalPrice}", name = "Payment", icon = { painterResource(id = R.drawable.user) })
//    object RoomDetail : Screen(route = "RoomDetail", name = "Roomdetail", icon = { painterResource(id = R.drawable.user) })
object RoomDetail : Screen(
    route = "RoomDetail/{encodedRoomType}/{checkin}/{checkout}/{pet}/{pricePerDay}/{roomId}",
    name = "RoomDetail",
    icon = { painterResource(id = R.drawable.user) }
)




    object Search : Screen(route = "Search", name="Search",icon = {
        remember {
            object : Painter() {
                override val intrinsicSize = Size(1f, 1f)
                override fun DrawScope.onDraw() { /* ไม่วาดอะไร */ }
        }
     }})

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

    object Booking : Screen(
        route = "Booking",
        name = "Booking",
        icon = {
            remember {
                object : Painter() {
                    override val intrinsicSize = Size(1f, 1f)
                    override fun DrawScope.onDraw() {  }
                }
            }
        }
    )

    object Showroom : Screen(
        route = "Showroom",
        name = "Showroom",
        icon = {
            remember {
                object : Painter() {
                    override val intrinsicSize = Size(1f, 1f)
                    override fun DrawScope.onDraw() {  }
                }
            }
        }
    )

    object EditProfile : Screen(
        route = "EditProfile",
        name = "EditProfile",
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
