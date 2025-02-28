package com.example.ass07.customer.Home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ass07.R
import com.example.ass07.admin.Room
import com.example.ass07.admin.RoomType
import com.example.ass07.customer.API.SearchApi
import com.example.ass07.customer.BB.Companion.MyBottomBar
import com.example.ass07.customer.BB.Companion.MyTopAppBar
import com.example.ass07.customer.Mypet.PetType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun Search(
    navController: NavHostController,
    pet: Int,
    checkin: String,
    checkout: String
) {
    val contextForToast = LocalContext.current.applicationContext

    Scaffold(
        topBar = {
            MyTopAppBar(navController, contextForToast)
        },
        bottomBar = {
            MyBottomBar(navController, contextForToast)
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                var availableRooms by remember { mutableStateOf<List<Room>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }
                var errorMessage by remember { mutableStateOf("") }

                // ดึงชื่อสัตว์จากหมายเลข
                val petType = when (pet) {
                    1 -> "สุนัข"
                    2 -> "แมว"
                    3 -> "นก"
                    else -> "ไม่ทราบ"
                }

                LaunchedEffect(key1 = pet, key2 = checkin, key3 = checkout) {
                    fetchAvailableRooms(checkin, checkout, pet) { rooms, error ->
                        if (error.isNullOrEmpty()) {
                            availableRooms = rooms
                        } else {
                            errorMessage = error
                        }
                        isLoading = false
                    }
                }

                val formattedCheckIn = convertDateToMonthName(checkin)
                val formattedCheckOut = convertDateToMonthName(checkout)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                        ,colors = CardDefaults.cardColors(Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp), // เพิ่ม padding ภายใน card
                            horizontalAlignment = Alignment.Start // จัดข้อความให้อยู่ทางซ้าย
                        ) {
                            Text(
                                text = "ประเภท: $petType",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp) // เพิ่มช่องว่างระหว่างข้อความ
                            )
                            Text(
                                text = "วันที่: $formattedCheckIn - $formattedCheckOut",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    }

                    if (isLoading) {
                        Text("กำลังโหลดข้อมูลห้องว่าง...")
                    } else {
                        if (errorMessage.isNotEmpty()) {
                            Text("เกิดข้อผิดพลาด: $errorMessage", color = Color.Red)
                        } else {
                            LazyColumn {
                                items(availableRooms) { room ->
                                    // เลือกภาพตามประเภทห้อง
                                    val roomImage = when (room.name_type) {
                                        "Deluxe Dog Room" -> R.drawable.room_deluxe
                                        "Standard Cat Room" -> R.drawable.room_standard
                                        "Bird Cage" -> R.drawable.room_bird
                                        else -> R.drawable.test
                                    }

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                        ,colors = CardDefaults.cardColors(Color.White),
                                        shape = RoundedCornerShape(8.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                    ) {
                                        Row(modifier = Modifier.padding(16.dp)) {
                                            // แสดงภาพห้อง
                                            Image(
                                                painter = painterResource(id = roomImage),
                                                contentDescription = "Room Image",
                                                modifier = Modifier
                                                    .padding(3.dp)
                                                    .size(120.dp)
                                            )

                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Text("${room.name_type}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                                Text("ห้องกว้าง | อากาศถ่ายเท | ห้องสะอาด", fontSize = 12.sp)
                                                Text("THB ${room.price_per_day}", fontSize = 20.sp,textAlign=TextAlign.End)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

fun convertDateToMonthName(date: String): String {
    // สร้าง SimpleDateFormat เพื่อแปลงวันที่เป็น Date object
    val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale("th", "TH")) // กำหนดให้ใช้ชื่อเดือนภาษาไทย

    return try {
        val parsedDate = inputFormat.parse(date) // แปลง string เป็น Date
        outputFormat.format(parsedDate) // แปลง Date กลับเป็น string ในรูปแบบที่ต้องการ
    } catch (e: Exception) {
        e.printStackTrace()
        date
    }
}

fun fetchAvailableRooms(
    checkIn: String,
    checkOut: String,
    petType: Int,
    callback: (List<Room>, String?) -> Unit
) {
    // สร้าง Retrofit instance
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/") // เปลี่ยน URL ให้ตรงกับเซิร์ฟเวอร์ของคุณ
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(SearchApi::class.java)

    // เรียกใช้ API โดยส่งค่า check_in, check_out, pet_type_id
    val call = apiService.getAvailableRooms(checkIn, checkOut, petType)
    call.enqueue(object : Callback<AvailableRoomsResponse> {
        override fun onResponse(
            call: Call<AvailableRoomsResponse>,
            response: Response<AvailableRoomsResponse>
        ) {
            if (response.isSuccessful) {
                callback(response.body()?.available_rooms ?: emptyList(), null)
            } else {
                callback(emptyList(), "เกิดข้อผิดพลาดในการเรียก API")
            }
        }

        override fun onFailure(call: Call<AvailableRoomsResponse>, t: Throwable) {
            callback(emptyList(), t.message)
        }
    })
}

