package com.example.ass07.admin.booking

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun BookingDetail(bookingId: Int) {
    val bookingApi = BookingAPI.create()
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var totalDays by remember { mutableIntStateOf(0) }
    var totalPrice by remember { mutableIntStateOf(0) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showExtendDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var refreshTrigger by remember { mutableStateOf(false) } // refresh ข้อมูลหลังจากกด checkout, extend

    LaunchedEffect(bookingId, refreshTrigger) {
        isLoading = true
        bookingApi.getBookingById(bookingId).enqueue(object : Callback<Booking> {
            override fun onResponse(call: Call<Booking>, response: Response<Booking>) {
                if (response.isSuccessful) {
                    booking = response.body()
                    booking?.let {
                        totalPrice = it.pay + (it.adjust ?: 0)
                        totalDays = calculateNumOfDays(it.checkIn, it.checkOut).toInt()
                    }
                }
                isLoading = false
            }

            override fun onFailure(call: Call<Booking>, t: Throwable) {
                isLoading = false
            }
        })
    }


    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            booking?.let { bookingData ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Booking ID Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "รหัสการจอง",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "#${bookingData.bookingId}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            StatusBadge(bookingData.status)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Owner Information Section
                    SectionCard(
                        icon = Icons.Filled.Person,
                        title = "ข้อมูลเจ้าของ",
                        content = {
                            DetailRow("ชื่อ", bookingData.name)
                            DetailRow("เบอร์โทร", bookingData.tellNumber)
                            DetailRow("อีเมล", bookingData.email)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Pet Information Section
                    SectionCard(
                        icon = Icons.Filled.Info,
                        title = "ข้อมูลสัตว์เลี้ยง",
                        content = {
                            DetailRow("ชื่อ", bookingData.petName)
                            DetailRow("ประเภท", bookingData.petNameType)
                            DetailRow("สายพันธุ์", bookingData.petBreed)
                            DetailRow("อายุ", "${bookingData.petAge} ปี")
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Booking Details Section
                    SectionCard(
                        icon = Icons.Filled.Home,
                        title = "รายละเอียดการเข้าพัก",
                        content = {
                            DetailRow("ประเภทห้อง", bookingData.roomType)
                            DetailRow("วันที่เช็คอิน", formatDateTime(bookingData.checkIn)+" น.")
                            DetailRow("วันที่เช็คเอาท์", formatDateTime(bookingData.checkOut)+" น." )
                            DetailRow("จำนวนวัน", "$totalDays วัน")
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price Summary Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "สรุปค่าใช้จ่าย",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "ค่าห้องพัก (${bookingData.pricePerDay} บาท/วัน)\n[ก่อนขยายเวลา]",
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "${bookingData.pay} บาท",
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (bookingData.adjust != null && bookingData.adjust != 0) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "ค่าใช้จ่ายเพิ่มเติม",
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                    Text(
                                        text = "${bookingData.adjust} บาท",
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Text(
                                    text = "รวมทั้งสิ้น",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "$totalPrice บาท",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }

                    // Action Buttons for status 1 (checked in)
                    AnimatedVisibility(visible = bookingData.status == 1) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Button(
                                onClick = { showCheckoutDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("เช็คเอาท์")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showExtendDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {
                                Icon(
                                    Icons.Filled.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("ขยายเวลา")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            } ?: run {
                ErrorMessage()
            }
        }
    }

    if (showCheckoutDialog) {
        CheckoutConfirmationDialog(
            onConfirm = {
                val bookingApi = BookingAPI.create()
                val statusUpdate = mapOf("booking_status" to 2)

                bookingApi.updateBooking(bookingId, statusUpdate).enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "เช็คเอาท์สำเร็จ", Toast.LENGTH_SHORT).show()
                            refreshTrigger = !refreshTrigger  // รีเฟรชข้อมูล
                        } else {
                            Toast.makeText(context, "เช็คเอาท์ไม่สำเร็จ กรุณาลองใหม่", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        Toast.makeText(context, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

                showCheckoutDialog = false
            },
            onDismiss = { showCheckoutDialog = false }
        )
    }

    // ใน BookingDetail
    if (showExtendDialog) {
        ExtendStayDialog(
            pricePerDay = booking?.pricePerDay ?: 0,
            onConfirm = { days, cost ->
                val bookingApi = BookingAPI.create()
                val request = ExtendBookingRequest(days = days, additionalCost = cost)

                bookingApi.extendBooking(bookingId, request).enqueue(object : Callback<ExtendBookingResponse> {
                    override fun onResponse(call: Call<ExtendBookingResponse>, response: Response<ExtendBookingResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "ขยายเวลาเข้าพักสำเร็จ", Toast.LENGTH_SHORT).show()
                            refreshTrigger = !refreshTrigger  // รีเฟรชข้อมูล
                        } else {
                            Toast.makeText(context, "ไม่สามารถขยายเวลาเข้าพักได้", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ExtendBookingResponse>, t: Throwable) {
                        Toast.makeText(context, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

                showExtendDialog = false
            },
            onDismiss = { showExtendDialog = false }
        )
    }
}

@Composable
fun SectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(  // เพิ่มเงา
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StatusBadge(status: Int) {
    val (statusText, color) = when (status) {
        0 -> "ยังไม่เช็คอิน" to MaterialTheme.colorScheme.primary
        1 -> "เช็คอินแล้ว" to Color(0xFF4CAF50)
        2 -> "เช็คเอาท์แล้ว" to Color(0xFF2196F3)
        3 -> "ยกเลิก" to Color(0xFFE91E63)
        else -> "ไม่ระบุ" to Color.Gray
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = statusText,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ErrorMessage() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "ไม่พบข้อมูลการจอง",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
@Composable
fun CheckoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ยืนยันการเช็คเอาท์") },
        text = { Text("คุณต้องการเช็คเอาท์การจองนี้ใช่หรือไม่?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("ยืนยัน")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("ยกเลิก")
            }
        }
    )
}

@Composable
fun ExtendStayDialog(
    pricePerDay: Int,
    onConfirm: (days: Int, cost: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var additionalDays by remember { mutableStateOf("1") }
    val additionalCost = (additionalDays.toIntOrNull() ?: 0) * pricePerDay

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ขยายเวลาเข้าพัก") },
        text = {
            Column {
                Text("กรุณาระบุจำนวนวันที่ต้องการขยายเวลา")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = additionalDays,
                    onValueChange = { if (it.toIntOrNull() != null) additionalDays = it },
                    label = { Text("จำนวนวัน") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (additionalDays.toIntOrNull() != null && additionalDays.toInt() > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ค่าใช้จ่ายเพิ่มเติม: $additionalCost บาท")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val days = additionalDays.toIntOrNull() ?: 1
                    onConfirm(days, additionalCost)
                },
                enabled = additionalDays.toIntOrNull() != null && additionalDays.toInt() > 0
            ) {
                Text("ยืนยัน")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("ยกเลิก")
            }
        }
    )
}