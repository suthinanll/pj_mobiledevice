package com.example.ass07.customer.Home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.ass07.R
import com.example.ass07.admin.Room
import com.example.ass07.customer.API.PetApi
import com.example.ass07.customer.API.SearchApi
import com.example.ass07.customer.Mypet.PetType
import com.example.ass07.customer.Screen
import com.example.ass07.customer.convertDateToMonthName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
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
    var availableRooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    var context = LocalContext.current
    var petTypes by remember { mutableStateOf<List<PetType>>(emptyList()) }
    val petApi = PetApi.create()
// โหลดข้อมูลจาก API ใน LaunchedEffect
    LaunchedEffect(Unit) {
        petApi.getPetTypes().enqueue(object : Callback<List<PetType>> {
            override fun onResponse(call: Call<List<PetType>>, response: Response<List<PetType>>) {
                if (response.isSuccessful) {
                    petTypes = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "โหลดข้อมูลสัตว์เลี้ยงล้มเหลว", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PetType>>, t: Throwable) {
                Toast.makeText(context, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Log.e("Data:","$pet $checkin $checkout")

    // ดึงชื่อสัตว์จากหมายเลข
// ค้นหาชื่อประเภทสัตว์เลี้ยงจาก API โดยใช้ pet_type_id
    val petType = petTypes.firstOrNull { it.Pet_type_id == pet }?.Pet_name_type ?: "ไม่ทราบ"

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
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ข้อมูลการจอง
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            colors = CardDefaults.cardColors(Color.White),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "ประเภท: $petType",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "วันที่: $formattedCheckIn - $formattedCheckOut",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
        }

        // แสดงข้อมูลห้อง
        if (isLoading) {
            Text("กำลังโหลดข้อมูลห้องว่าง...")
        } else {
            if (errorMessage.isNotEmpty()) {
                Text("เกิดข้อผิดพลาด: $errorMessage", color = Color.Red)
            } else {
                if (availableRooms.isEmpty()){

                    Text("ไม่มีห้องว่าง", color = Color.Red, fontSize = 32.sp)
                }
                 else{
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
                                .clickable {
                                    val encodedRoomType = URLEncoder.encode(room.name_type, "UTF-8")
                                    val roomId = room.room_id.toString()

                                    val days = calculateDays(checkin, checkout)
                                    val totalPrice = room.price_per_day?.times(days)

                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "room_data",room
                                    )

                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "checkin",checkin
                                    )

                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "checkout",checkout
                                    )

                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "pet",pet
                                    )

                                    navController.navigate(Screen.SearchDetail.route)
                                },
                            colors = CardDefaults.cardColors(Color.White),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                // แสดงภาพห้อง
                                if(room.image != null){
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = room.image
                                        ),
                                        contentDescription = "Room Image",
                                        modifier = Modifier
                                            .padding(3.dp)
                                            .size(120.dp)
                                    )
                                }else{
                                    Image(
                                        painter = painterResource( R.drawable.room_standard),
                                        contentDescription = "Room Image",
                                        modifier = Modifier
                                            .padding(3.dp)
                                            .size(120.dp)
                                    )
                                }

                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "${room.name_type}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "ห้องว่าง ${room.available}",
                                        fontSize = 12.sp
                                    )

                                    val days = calculateDays(checkin, checkout)
                                    val totalPrice = room.price_per_day?.times(days)


                                    Text(
                                        "THB ${room.price_per_day} / คืน",
                                        fontSize = 16.sp
                                    )

//
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

fun fetchAvailableRooms(
    checkIn: String,
    checkOut: String,
    petType: Int,
    callback: (List<Room>, String?) -> Unit
){
    val apiService = SearchApi.create()

    val call = apiService.getAvailableRooms(checkIn, checkOut, petType)
    call.enqueue(object : Callback<AvailableRoomsResponse> {
        override fun onResponse(
            call: Call<AvailableRoomsResponse>,
            response: Response<AvailableRoomsResponse>
        ) {
            if (response.isSuccessful) {
                callback(response.body()?.available_rooms ?: emptyList(), null)
                Log.e("Result",response.body()?.available_rooms.toString())
            } else {
                callback(emptyList(), "เกิดข้อผิดพลาดในการเรียก API")
            }
        }

        override fun onFailure(call: Call<AvailableRoomsResponse>, t: Throwable) {
            callback(emptyList(), t.message)
        }
    })
}

fun calculateDays(checkInStr: String, checkOutStr: String): Int {
    if (checkInStr.isEmpty() || checkOutStr.isEmpty()) {
        return 1 // Default to 1 day if dates are missing
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return try {
        val checkInDate = dateFormat.parse(checkInStr)
        val checkOutDate = dateFormat.parse(checkOutStr)
        val diffInMillis = checkOutDate.time - checkInDate.time
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
        diffInDays.toInt()
    } catch (e: Exception) {
        return  1
    }
}
