package com.example.ass07.customer

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.ass07.R
import com.example.ass07.admin.Room
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun HotelBookingScreen(
    navController: NavHostController
) {
    val room = navController.previousBackStackEntry?.savedStateHandle?.get<Room>("room_data")
    val checkin = navController.previousBackStackEntry?.savedStateHandle?.get<String>("checkin")
    val checkout = navController.previousBackStackEntry?.savedStateHandle?.get<String>("checkout")
    val pet = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("pet")
    val totalPrice = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("total_price")
    val petTypeName = when (pet ?: 0) {
        1 -> "สุนัข"
        2 -> "แมว"
        3 -> "นก"
        else -> "ไม่ทราบ"
    }

    val roomImage = when (room?.name_type) {
        "Deluxe Dog Room" -> R.drawable.room_deluxe
        "Standard Cat Room" -> R.drawable.room_standard
        "Bird Cage" -> R.drawable.room_bird
        else -> R.drawable.test
    }


    val formattedCheckIn = convertDateToMonthName(checkin ?: "")
    val formattedCheckOut = convertDateToMonthName(checkout ?: "")



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFDF8EC))
    )
    {
        BookingHeader(room?.name_type ?: "", petTypeName, formattedCheckIn, formattedCheckOut)

        Column(modifier = Modifier.fillMaxWidth()) {
            if(room?.image != null){
                Image(
                    painter = rememberAsyncImagePainter(
                        model = room.image
                    ),
                    contentDescription = "Main Room Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }else{
                Image(
                    painter = painterResource(R.drawable.room_standard),
                    contentDescription = "Main Room Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        HotelDetailSection(room?.name_type ?: "", room?.price_per_day.toString() ?: "0")
//        BookingButton(navController, room?.room_id.toString() ?: "", room?.name_type.toString() ?: "",
//            formattedCheckIn, formattedCheckOut, petTypeName, room?.price_per_day.toString() ?: "0")

        Button(
            onClick = {
                // ส่งข้อมูลไปยัง BookingScreen
                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "booking_data",
                    BookingClass(
                        roomId = room?.room_id.toString() ?: "",
                        roomType = room?.name_type.toString() ?: "",
                        checkInDate = formattedCheckIn,
                        checkOutDate = formattedCheckOut,
                        petType = petTypeName,
                        price = room?.price_per_day.toString() ?: "0",
                        image = room?.image ?: ""
                    )
                )

                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "checkin" , checkin
                )

                navController.currentBackStackEntry?.savedStateHandle?.set(
                    "checkout",checkout
                )

                navController.currentBackStackEntry?.savedStateHandle?.set("pet",pet)

                // ส่ง totalPrice ไปยัง BookingScreen
                navController.currentBackStackEntry?.savedStateHandle?.set("total_price", totalPrice)

                // ส่งจำนวนวัน (days) ไปยัง BookingScreen
                navController.currentBackStackEntry?.savedStateHandle?.set("days", calculateDays(formattedCheckIn, formattedCheckOut))

                // นำทางไปยัง BookingScreen
                navController.navigate("BookingInfo")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
        ) {
            Text(text = "จองห้องพัก", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun BookingHeader(roomType: String, petType: String, checkIn: String, checkOut: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "ประเภท: $petType", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = "$checkIn - $checkOut", fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun HotelImageSection(roomImage: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = roomImage),
            contentDescription = "Main Room Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun HotelDetailSection(roomType: String, price: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = roomType, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Column {
                    Text(
                        text = "THB $price",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ให้สิ่งที่ดีที่สุดของคุณกับประสบการณ์การพักผ่อนที่ดีที่สุดของเรา ห้องกว้างขวาง สะอาด และสะดวกสบาย",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "บริการสำหรับห้อง $roomType", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Column {
                when {
                    roomType.contains("Dog") -> {
                        Text("✔ พื้นที่วิ่งเล่นสำหรับสุนัข", fontSize = 14.sp)
                        Text("✔ ห้องอาบน้ำสัตว์เลี้ยง", fontSize = 14.sp)
                        Text("✔ เครื่องปรับอากาศ", fontSize = 14.sp)
                        Text("✔ อาหารเช้าสำหรับสุนัข", fontSize = 14.sp)
                    }
                    roomType.contains("Cat") -> {
                        Text("✔ ต้นไม้สำหรับแมวปีน", fontSize = 14.sp)
                        Text("✔ กระบะทรายสะอาด", fontSize = 14.sp)
                        Text("✔ เครื่องปรับอากาศ", fontSize = 14.sp)
                        Text("✔ อาหารเช้าสำหรับแมว", fontSize = 14.sp)
                    }
                    roomType.contains("Bird") -> {
                        Text("✔ กรงขนาดใหญ่", fontSize = 14.sp)
                        Text("✔ คอนเกาะหลากหลายระดับ", fontSize = 14.sp)
                        Text("✔ อากาศถ่ายเทดี", fontSize = 14.sp)
                        Text("✔ อาหารและน้ำสะอาด", fontSize = 14.sp)
                    }
                    else -> {
                        Text("✔ ทีวีและอินเทอร์เน็ต", fontSize = 14.sp)
                        Text("✔ ห้องอาบน้ำส่วนตัว", fontSize = 14.sp)
                        Text("✔ เครื่องปรับอากาศ", fontSize = 14.sp)
                        Text("✔ อาหารเช้า และเตียงนอน", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Parcelize
data class BookingClass(
    val roomId: String,
    val roomType: String,
    val checkInDate: String,
    val checkOutDate: String,
    val petType: String,
    val price: String,
    val image : String
) : Parcelable

@Composable
fun BookingButton(
    navController: NavHostController,
    roomId: String,
    roomType: String,
    checkInDate: String,
    checkOutDate: String,
    petType: String,
    price: String,
    image: String,
) {
    Button(
        onClick = {
            // คำนวณ totalPrice
            val totalPrice = price.toDouble() * calculateDays(checkInDate, checkOutDate)

            // ส่งข้อมูลไปยัง BookingScreen
            navController.currentBackStackEntry?.savedStateHandle?.set(
                "booking_data",
                BookingClass(
                    roomId = roomId,
                    roomType = roomType,
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    petType = petType,
                    price = price,
                    image = image
                )
            )


            // ส่ง totalPrice ไปยัง BookingScreen
            navController.currentBackStackEntry?.savedStateHandle?.set("total_price", totalPrice)

            // ส่งจำนวนวัน (days) ไปยัง BookingScreen
            navController.currentBackStackEntry?.savedStateHandle?.set("days", calculateDays(checkInDate, checkOutDate))


            navController.navigate(Screen.BookingInfo.route)

        },

    ) {
    }
}

fun calculateDays(checkIn: String, checkOut: String): Int {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("th", "TH"))
    return try {
        val checkInDate = dateFormat.parse(checkIn)
        val checkOutDate = dateFormat.parse(checkOut)
        val diff = checkOutDate.time - checkInDate.time
        TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
    } catch (e: Exception) {
        e.printStackTrace()
        1
    }
}

fun convertDateToMonthName(date: String): String {
    val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("th", "TH"))
    return try {
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate)
    } catch (e: Exception) {
        e.printStackTrace()
        date
    }
}