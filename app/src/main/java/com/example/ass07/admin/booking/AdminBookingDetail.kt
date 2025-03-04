package com.example.ass07.admin.booking

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AttachMoney
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// ประกาศสีหลักที่ใช้ในแอพพลิเคชัน
private val primaryBackground = Color(0xFFF8F0E5) // สีพื้นหลักโทนครีม
private val cardBackground = Color(0xFFFFFFFF) // สีพื้นการ์ด
private val primaryColor = Color(0xFF855B41) // สีหลัก (น้ำตาล)
private val accentColor = Color(0xFFDCB996) // สีรอง (น้ำตาลอ่อน)
private val checkoutColor = Color(0xFFE74C3C) // สีแดงสำหรับเช็คเอาท์
private val extendColor = Color(0xFFF39C12) // สีส้มสำหรับขยายเวลา

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
        modifier = Modifier
            .fillMaxSize()
            .background(primaryBackground)
    ) { paddingValues ->
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            booking?.let { bookingData ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(primaryBackground)
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Booking ID Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
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
                                    color = primaryColor.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "#${bookingData.bookingId}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
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
                            containerColor = cardBackground
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = accentColor.copy(alpha = 0.3f)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AttachMoney,
                                    contentDescription = "Total Cost",
                                    tint = primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ข้อมูลการชำระเงิน",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            ExpenseItem(
                                title = "วิธีการชำระเงิน",
                                amount = "${bookingData.methodName}"
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            ExpenseItem(
                                title = "ค่าห้องพัก (${bookingData.pricePerDay} บาท/วัน)",
                                subtitle = "[ก่อนขยายเวลา]",
                                amount = "${bookingData.pay} บาท"
                            )

                            if (bookingData.adjust != null && bookingData.adjust != 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                ExpenseItem(
                                    title = "ค่าใช้จ่ายเพิ่มเติม",
                                    amount = "${bookingData.adjust} บาท"
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = primaryColor.copy(alpha = 0.1f), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "รวมทั้งสิ้น",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                                Text(
                                    text = "$totalPrice บาท",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
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
                                    containerColor = checkoutColor
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("เช็คเอาท์", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { showExtendDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = extendColor
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    Icons.Filled.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text("ขยายเวลา", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
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

    if (showExtendDialog) {
        booking?.let { bookingData ->
            ExtendStayDialog(
                pricePerDay = bookingData.pricePerDay,
                currentCheckOut = bookingData.checkOut,
                onConfirm = { days, cost ->
                    val bookingApi = BookingAPI.create()
                    val request = ExtendBookingRequest(days = days, additionalCost = cost)

                    bookingApi.extendBooking(bookingId, request).enqueue(object : Callback<ExtendBookingResponse> {
                        override fun onResponse(call: Call<ExtendBookingResponse>, response: Response<ExtendBookingResponse>) {
                            if (response.isSuccessful) {
                                val resultData = response.body()
                                if (resultData != null) {
                                    // แสดงข้อมูลที่อัพเดตสำเร็จ
                                    Toast.makeText(
                                        context,
                                        "ขยายเวลาเข้าพักสำเร็จ (${days-1} วัน, ${cost} บาท)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    refreshTrigger = !refreshTrigger  // รีเฟรชข้อมูล
                                } else {
                                    Toast.makeText(context, "ไม่สามารถขยายเวลาเข้าพักได้: ไม่พบข้อมูลตอบกลับ", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorMsg = try {
                                    response.errorBody()?.string() ?: "ไม่สามารถขยายเวลาเข้าพักได้"
                                } catch (e: Exception) {
                                    "ไม่สามารถขยายเวลาเข้าพักได้: ${response.code()}"
                                }
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
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
}

@Composable
private fun ExpenseItem(
    title: String,
    amount: String,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = title,
                color = Color.Black.copy(alpha = 0.7f)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.5f)
                )
            }
        }
        Text(
            text = amount,
            fontWeight = FontWeight.Medium,
            color = Color.Black
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
            containerColor = cardBackground
        ),
        border = BorderStroke(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
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
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
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
            color = Color.Black.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
fun StatusBadge(status: Int) {
    val (statusText, color) = when (status) {
        0 -> "ยังไม่เช็คอิน" to Color.Gray
        1 -> "เช็คอินแล้ว" to Color(0xFF4CAF50) // สีเขียว
        2 -> "เช็คเอาท์แล้ว" to Color(0xFF2196F3) // สีฟ้า
        3 -> "ยกเลิก" to Color(0xFFE74C3C) // สีแดง
        else -> "ไม่ระบุ" to Color.Gray
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = statusText,
            color = color,
            fontWeight = FontWeight.Bold,
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
            .background(primaryBackground)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.Warning,
                contentDescription = "Error",
                tint = checkoutColor,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = "ไม่พบข้อมูลการจอง",
                style = MaterialTheme.typography.titleMedium,
                color = primaryColor
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
        containerColor = cardBackground,
        titleContentColor = primaryColor,
        textContentColor = Color.Black.copy(alpha = 0.7f),
        title = { Text("ยืนยันการเช็คเอาท์", fontWeight = FontWeight.Bold) },
        text = { Text("คุณต้องการเช็คเอาท์การจองนี้ใช่หรือไม่?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = checkoutColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("ยืนยัน", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, primaryColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("ยกเลิก")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtendStayDialog(
    pricePerDay: Int,
    currentCheckOut: String,
    onConfirm: (days: Int, cost: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    // เช็คว่าเลือกวันหรือยัง
    var isDateSelected by remember { mutableStateOf(false) }

    // Parse current checkout date
    val currentCheckOutDateTime = remember {
        try {
            LocalDateTime.parse(currentCheckOut, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            // Fallback to current date if parsing fails
            LocalDateTime.now()
        }
    }

    // Initial selected date is current checkout + 1 day
    var selectedDate by remember {
        mutableStateOf(currentCheckOutDateTime.plusDays(1))
    }

    // Calculate days difference between current checkout and selected date
    val daysDifference = remember(selectedDate) {
        ChronoUnit.DAYS.between(currentCheckOutDateTime.toLocalDate(), selectedDate.toLocalDate()).toInt()
    }

    // Calculate additional cost based on days difference
    val additionalCost = (daysDifference-1) * pricePerDay

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ขยายเวลาเข้าพัก") },
        text = {
            Column {
                Text("กรุณาเลือกวันที่ต้องการ check-out ใหม่")
                Spacer(modifier = Modifier.height(8.dp))

                // โชว์ วัน checkout เดิม
                DetailRow(
                    label = "วันที่ check-out ปัจจุบัน",
                    value = formatDateTime(currentCheckOut)
                )

                // open date picker
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "เลือกวันที่",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("เลือกวันที่ check-out ใหม่")
                }

                // โชว์วัน checkout ใหม่
                DetailRow(
                    label = "วันที่ check-out ใหม่",
                    value = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"))
                )

                // Show days difference
                if (daysDifference > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("จำนวนวันที่ขยาย: ${daysDifference-1} วัน")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("ค่าใช้จ่ายเพิ่มเติม: $additionalCost บาท")
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "กรุณาเลือกวันที่มากกว่าวันที่ check-out ปัจจุบัน",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (daysDifference > 0) {
                        onConfirm(daysDifference, additionalCost)
                    }
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = extendColor),
                enabled = daysDifference > 0
            ) {
                Text("ยืนยัน")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss,
                border = BorderStroke(1.dp, primaryColor),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                shape = RoundedCornerShape(8.dp)
                ) {
                Text("ยกเลิก")
            }
        }
    )

    // Date Picker Dialog
    if (showDatePicker) {
        // Convert LocalDateTime to millis for DatePicker
        val initialSelectedDateMillis = selectedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Convert selected millis back to LocalDateTime
                        val newDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                        // Keep the time part from the original selection
                        selectedDate = LocalDateTime.of(
                            newDate.toLocalDate(),
                            currentCheckOutDateTime.toLocalTime()
                        )
                    }
                    showDatePicker = false
                },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = extendColor)
                    ) {
                    Text("ตกลง")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDatePicker = false },
                    border = BorderStroke(1.dp, primaryColor),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                    shape = RoundedCornerShape(8.dp)
                    ) {
                    Text("ยกเลิก")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
