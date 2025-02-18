package com.example.ass07.admin

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun BookingDetail(bookingId: Int) {
    //val context = LocalContext.current
    val bookingApi = BookingAPI.create()
    //val coroutineScope = rememberCoroutineScope()

    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var totalDays by remember { mutableIntStateOf(0) }
    var totalPrice by remember { mutableIntStateOf(0) }

    LaunchedEffect(bookingId) {
        bookingApi.getBookingById(bookingId).enqueue(object : Callback<Booking> {
            override fun onResponse(call: Call<Booking>, response: Response<Booking>) {
                if (response.isSuccessful) {
                    booking = response.body()
                    booking?.let {
                        totalPrice = it.pay + (it.adjust ?: 0) // รวมค่า adjust ในราคารวม
                        totalDays = if (it.pay != null && it.pricePerDay > 0) {
                            it.pay / it.pricePerDay
                        } else {
                            0
                        }
                    }
                } else {
                    Log.e("BookingDetail", "Error: ${response.message()}")
                }
                isLoading = false
            }

            override fun onFailure(call: Call<Booking>, t: Throwable) {
                Log.e("BookingDetail", "API call failed: ${t.message}")
                isLoading = false
            }
        })
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            booking?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    HeaderSection(it.bookingId)
                    Spacer(modifier = Modifier.height(16.dp))
                    StatusCard(it.status)
                    Spacer(modifier = Modifier.height(16.dp))

                    InfoCard(
                        title = "ข้อมูลการจอง",
                        content = {
                            InfoRow(
                                icon = Icons.Default.Person,
                                label = "เจ้าของ",
                                value = "${it.name} (${it.tellNumber})"
                            )

                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            InfoRow(
                                icon = Icons.Outlined.ShoppingCart,
                                label = "สัตว์เลี้ยง",
                                value = "${it.petName} (${it.petBreed}, ${it.petAge} ปี)"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoCard(
                        title = "รายละเอียดการเข้าพัก",
                        content = {
                            InfoRow(
                                icon = Icons.Default.DateRange,
                                label = "วันเช็คอิน",
                                value = it.checkIn
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            InfoRow(
                                icon = Icons.Default.DateRange,
                                label = "วันเช็คเอาท์",
                                value = it.checkOut
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            InfoRow(
                                icon = Icons.Default.Home,
                                label = "ประเภทห้อง",
                                value = it.roomType
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            InfoRow(
                                icon = Icons.Outlined.ShoppingCart,
                                label = "วิธีการชำระเงิน",
                                value = it.methodName ?: "-"
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            InfoRow(
                                icon = Icons.Outlined.Info,
                                label = "ข้อมูลเพิ่มเติม",
                                value = it.additionalInfo ?: "-"
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PriceCard(
                        roomType = it.roomType,
                        pricePerDay = it.pricePerDay,
                        totalDays = totalDays,
                        totalPrice = totalPrice,
                        adjust = it.adjust ?: 0
                    )
                }
            } ?: Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = "Not Found",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "ไม่พบข้อมูลการจอง",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderSection(bookingId: Int) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "รายละเอียดการจอง",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "รหัสการจอง: #$bookingId",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatusCard(status: Int) {
    val statusInfo = when (status) {
        0 -> Triple("ยังไม่เช็คอิน", MaterialTheme.colorScheme.primary, Color.White)
        1 -> Triple("เช็คอินแล้ว", Color(0xFF4CAF50), Color.White)
        2 -> Triple("เช็คเอาท์แล้ว", Color(0xFF2196F3), Color.White)
        3 -> Triple("ยกเลิก", Color(0xFFE91E63), Color.White)
        else -> Triple("ไม่ระบุ", Color.Gray, Color.White)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(statusInfo.second.copy(alpha = 0.2f))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(statusInfo.second)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusInfo.first,
                    color = statusInfo.third,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
            Text(
                text = getStatusDescription(status),
                modifier = Modifier.padding(start = 12.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun InfoCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PriceCard(
    roomType: String,
    pricePerDay: Int,
    totalDays: Int,
    totalPrice: Int,
    adjust: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "สรุปค่าใช้จ่าย",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ประเภทห้อง",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = roomType,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ราคาต่อวัน",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${pricePerDay.toInt()} บาท",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "จำนวนวัน",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$totalDays วัน",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            if (adjust != 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ค่าใช้จ่ายอื่นๆ",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$adjust บาท",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ราคารวมทั้งสิ้น",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${totalPrice.toInt()} บาท",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

fun getStatusDescription(status: Int): String {
    return when (status) {
        0 -> "รอเช็คอินในวันที่กำหนด"
        1 -> "สัตว์เลี้ยงอยู่ในการดูแลของเรา"
        2 -> "การเข้าพักเสร็จสิ้นแล้ว"
        3 -> "การจองถูกยกเลิก"
        else -> "สถานะไม่ระบุ"
    }
}
