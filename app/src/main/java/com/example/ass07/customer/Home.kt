package com.example.ass07.customer

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
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    var selectedPet by remember { mutableStateOf("เลือกประเภทสัตว์เลี้ยงของคุณ") }
    var checkInDate by remember { mutableStateOf("วว/ดด/ปปปป") }
    var checkOutDate by remember { mutableStateOf("วว/ดด/ปปปป") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(alignment = Alignment.CenterHorizontally),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(corner = CornerSize(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ค้นหาห้องพัก",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = "เลือกประเภทสัตว์เลี้ยง",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(10.dp))

                PetDropdownMenu(
                    selectedPet = selectedPet,
                    onPetSelected = { pet -> selectedPet = pet }
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = "เช็คอิน",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                DateCheckinField(
                    selectedDate = checkInDate,
                    onDateSelected = { date -> checkInDate = date } // อัปเดตวันที่ใน State
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    modifier = Modifier.align(Alignment.Start),
                    text = "เช็คเอาท์",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                DateCheckoutField(
                    selectedDate = checkOutDate,
                onDateSelected = { date -> checkOutDate = date }
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(Color(0xFFFFBC2B)),
                    shape = RoundedCornerShape(corner = CornerSize(10.dp))
                ) {Text(
                    text="ค้นหาห้องพัก",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDropdownMenu(
    selectedPet: String,
    onPetSelected: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val petsList = listOf("สุนัข", "แมว", "กระต่าย")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = Modifier.clickable { keyboardController?.hide() },
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .width(340.dp)
                .menuAnchor()
                .clickable { keyboardController?.hide() },
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
fun DateCheckinField(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val mCalendar = Calendar.getInstance()
    val mYear = mCalendar[Calendar.YEAR]
    val mMonth = mCalendar[Calendar.MONTH]
    val mDay = mCalendar[Calendar.DAY_OF_MONTH]

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            onDateSelected(dateFormat.format(newDate.time)) // อัปเดตวันที่
        },
        mYear, mMonth, mDay
    )

    OutlinedTextField(
        value = selectedDate,
        shape = RoundedCornerShape(corner = CornerSize(10.dp)),
        onValueChange = {},
        readOnly = true,
        label = {},
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = "Select Date",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        mDatePickerDialog.show() // กดที่ไอคอนแล้วแสดง DatePicker
                    }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                mDatePickerDialog.show() // กดที่ Field แล้วแสดง DatePicker
            },
        colors = ExposedDropdownMenuDefaults.textFieldColors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateCheckoutField(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val mCalendar = Calendar.getInstance()
    val mYear = mCalendar[Calendar.YEAR]
    val mMonth = mCalendar[Calendar.MONTH]
    val mDay = mCalendar[Calendar.DAY_OF_MONTH]

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val newDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            onDateSelected(dateFormat.format(newDate.time)) // อัปเดตวันที่
        },
        mYear, mMonth, mDay
    )

    OutlinedTextField(
        value = selectedDate,
        shape = RoundedCornerShape(corner = CornerSize(10.dp)),
        onValueChange = {},
        readOnly = true,
        label = {},
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.DateRange,
                contentDescription = "Select Date",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        mDatePickerDialog.show() // กดที่ไอคอนแล้วแสดง DatePicker
                    }
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                mDatePickerDialog.show() // กดที่ Field แล้วแสดง DatePicker
            },
        colors = ExposedDropdownMenuDefaults.textFieldColors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}
