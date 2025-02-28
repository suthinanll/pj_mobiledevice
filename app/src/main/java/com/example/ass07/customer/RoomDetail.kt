package com.example.ass07.customer

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ass07.R

@Composable
fun HotelBookingScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFDF8EC))
    ) {
        // Section: วันที่
        BookingHeader()

        // Section: รูปภาพห้อง
        HotelImageSection()

        // Section: รายละเอียดห้อง
        HotelDetailSection()

        // Section: ปุ่มจอง
        BookingButton(navController)
    }
}

@Composable
fun BookingHeader() {
    Spacer(modifier = Modifier.height(50.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "ประเภท: ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = "05 Dec - 08 Dec", fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun HotelImageSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.room5),
            contentDescription = "Main Room Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        LazyRow(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(listOf(R.drawable.room1, R.drawable.room2, R.drawable.room3, R.drawable.room4)) { image ->
                Image(
                    painter = painterResource(id = image),
                    contentDescription = "Room Image",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun HotelDetailSection() {
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
                    Text(text = "ดีลักซ์", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Row {
                        repeat(5) {

                        }
                    }
                }
                Column {
                    Text(
                        text = "THB 760",
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

            Text(text = "บริการสำหรับห้อง ดีลักซ์", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Column {
                Text("✔ ทีวีและอินเทอร์เน็ต", fontSize = 14.sp)
                Text("✔ ห้องอาบน้ำส่วนตัว", fontSize = 14.sp)
                Text("✔ เครื่องปรับอากาศ", fontSize = 14.sp)
                Text("✔ อาหารเช้า และเตียงนอน", fontSize = 14.sp)
            }

        }
    }
}

@Composable
fun BookingButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate(Screen.BookingInfo.route) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
    ) {
        Text(text = "จองห้องพัก", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHotelBookingScreen() {
    val navController = rememberNavController()
    HotelBookingScreen(navController)
}
