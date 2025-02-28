package com.example.ass07.customer.Home

import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ass07.customer.Screen
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavHostController) {
    var selectedPet by remember { mutableStateOf("") }
    var checkInDate by remember { mutableStateOf("") }
    var checkOutDate by remember { mutableStateOf("") }

    // เพิ่มการตรวจสอบข้อมูลครบถ้วน
    val isFormValid by remember(selectedPet, checkInDate, checkOutDate) {
        derivedStateOf {
            selectedPet.isNotEmpty() &&
                    selectedPet != "เลือกประเภทสัตว์เลี้ยงของคุณ" &&
                    checkInDate.isNotEmpty() &&
                    checkInDate != "วว/ดด/ปปปป" &&
                    checkOutDate.isNotEmpty() &&
                    checkOutDate != "วว/ดด/ปปปป" &&
                    isCheckoutAfterCheckin(checkInDate, checkOutDate)
        }
    }

    // เพิ่มตัวแปรเก็บข้อความแจ้งเตือน
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ค้นหาห้องพัก",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                FormFieldLabel(text = "เลือกประเภทสัตว์เลี้ยง")

                PetDropdownMenu(
                    selectedPet = if (selectedPet.isEmpty()) "เลือกประเภทสัตว์เลี้ยงของคุณ" else selectedPet,
                    onPetSelected = { pet -> selectedPet = pet }
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormFieldLabel(text = "วันที่เช็คอิน")

                DateField(
                    selectedDate = if (checkInDate.isEmpty()) "วว/ดด/ปปปป" else checkInDate,
                    onDateSelected = { date ->
                        checkInDate = date
                        // ตรวจสอบเมื่อเลือกวันเช็คอินแล้ววันเช็คเอาท์มีค่าแล้ว
                        if (checkOutDate.isNotEmpty() && checkOutDate != "วว/ดด/ปปปป") {
                            if (!isCheckoutAfterCheckin(date, checkOutDate)) {
                                errorMessage = "วันเช็คเอาท์ต้องมาหลังวันเช็คอิน"
                            } else {
                                errorMessage = ""
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormFieldLabel(text = "วันที่เช็คเอาท์")

                DateField(
                    selectedDate = if (checkOutDate.isEmpty()) "วว/ดด/ปปปป" else checkOutDate,
                    onDateSelected = { date ->
                        checkOutDate = date
                        // ตรวจสอบว่าวันเช็คเอาท์มาหลังวันเช็คอินหรือไม่
                        if (checkInDate.isNotEmpty() && checkInDate != "วว/ดด/ปปปป") {
                            if (!isCheckoutAfterCheckin(checkInDate, date)) {
                                errorMessage = "วันเช็คเอาท์ต้องมาหลังวันเช็คอิน"
                            } else {
                                errorMessage = ""
                            }
                        }
                    }
                )

                // แสดงข้อความแจ้งเตือนถ้ามี
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (isFormValid) {
                            val petNumber = when (selectedPet) {
                                "สุนัข" -> 1
                                "แมว" -> 2
                                "นก" -> 3
                                else -> 0
                            }

                            if (isFormValid) {
                                val checkinFormatted = URLEncoder.encode(checkInDate, "UTF-8")
                                val checkoutFormatted = URLEncoder.encode(checkOutDate, "UTF-8")
                                navController.navigate("search/${petNumber}/${checkinFormatted}/${checkoutFormatted}")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFBC2B),
                        disabledContainerColor = Color(0xFFDDDDDD)
                    ),
                    shape = RoundedCornerShape(corner = CornerSize(10.dp))
                ) {
                    Text(
                        text = "ค้นหาห้องพัก",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FormFieldLabel(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDropdownMenu(
    selectedPet: String,
    onPetSelected: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val petsList = listOf("สุนัข", "แมว", "นก")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
            keyboardController?.hide()
        }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(corner = CornerSize(10.dp)),
            readOnly = true,
            value = selectedPet,
            onValueChange = {},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            petsList.forEach { selectionOption ->
                DropdownMenuItem(
                    modifier = Modifier
                        .background(color = Color.White),
                    text = { Text(selectionOption) },
                    onClick = {
                        onPetSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            onDateSelected(dateFormat.format(newDate.time))
        },
        calendar[Calendar.YEAR],
        calendar[Calendar.MONTH],
        calendar[Calendar.DAY_OF_MONTH]
    )

    // ตั้งค่าวันที่ต่ำสุดเป็นวันปัจจุบัน (ไม่ให้เลือกวันในอดีต)
    mDatePickerDialog.datePicker.minDate = calendar.timeInMillis

    OutlinedTextField(
        value = selectedDate,
        shape = RoundedCornerShape(corner = CornerSize(10.dp)),
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = "Select Date",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        mDatePickerDialog.show()
                    }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                mDatePickerDialog.show()
            },
        colors = ExposedDropdownMenuDefaults.textFieldColors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

// ฟังก์ชันตรวจสอบว่าวันเช็คเอาท์มาหลังวันเช็คอินหรือไม่
fun isCheckoutAfterCheckin(checkinStr: String, checkoutStr: String): Boolean {
    if (checkinStr == "วว/ดด/ปปปป" || checkoutStr == "วว/ดด/ปปปป") return true

    try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val checkinDate: Date = dateFormat.parse(checkinStr) ?: return false
        val checkoutDate: Date = dateFormat.parse(checkoutStr) ?: return false

        return !checkoutDate.before(checkinDate)
    } catch (e: Exception) {
        return false
    }
}