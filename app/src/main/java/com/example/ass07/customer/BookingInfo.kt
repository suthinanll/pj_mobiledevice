
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ass07.R
import com.example.ass07.customer.API.PetApi
import com.example.ass07.customer.API.projectApi
import com.example.ass07.customer.BB
import com.example.ass07.customer.BB.Companion.MyScaffoldLayout
import com.example.ass07.customer.LoginRegister.SharePreferencesManager
import com.example.ass07.customer.Mypet.PetViewModel
import com.example.ass07.customer.Mypet.petMember
import com.example.ass07.customer.RoomPriceResponse
import com.example.ass07.customer.Screen
import com.example.ass07.ui.theme.ASS07Theme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


@Composable
fun BookingScreen(
    padding: Modifier = Modifier,
    navController: NavHostController,
    context: Context,
    roomId: Int,
    days: Int
) {

    var roomType by remember { mutableStateOf("สแตนดาร์ด") } // กำหนดค่าห้องเริ่มต้น
    var totalPrice by remember { mutableStateOf(0) }

    val preferencesManager = remember { SharePreferencesManager(context) }
    var priceResponse by remember { mutableStateOf<RoomPriceResponse?>(null) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var  petTypeName by remember { mutableStateOf("") }

//    val totalPrice = (priceResponse?.payPerNight ?: 0)// ✅ คำนวณราคารวม

    var checkInDate by remember { mutableStateOf<Date?>(null) }
    var checkOutDate by remember { mutableStateOf<Date?>(null) }
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var days by remember { mutableStateOf(1) }
    val checkInFormatted = checkInDate?.let { dateFormatter.format(it) } ?: ""
    val checkOutFormatted = checkOutDate?.let { dateFormatter.format(it) } ?: ""

    val userId = preferencesManager.userId ?: 0 // ดึง user_id จาก SharedPreferences
    var selectedPet by remember { mutableStateOf("") }

    LaunchedEffect(roomId, days) {
        val api = projectApi.create()
        api.getRoomPrice(roomId, days).enqueue(object : Callback<RoomPriceResponse> {
            override fun onResponse(call: Call<RoomPriceResponse>, response: Response<RoomPriceResponse>) {
                if (response.isSuccessful) {
                    priceResponse = response.body()
                }
            }
            override fun onFailure(call: Call<RoomPriceResponse>, t: Throwable) {
                Toast.makeText(context, "Failed to load price", Toast.LENGTH_SHORT).show()
            }
        })
    }

    LaunchedEffect(userId) {
        val api = PetApi.create()
        api.mypet(userId).enqueue(object : Callback<List<petMember>> {
            override fun onResponse(call: Call<List<petMember>>, response: Response<List<petMember>>) {
                if (response.isSuccessful) {
                    val pets = response.body()
                    petTypeName = pets?.firstOrNull()?.petName ?: "ไม่มีข้อมูลประเภทสัตว์เลี้ยง"
                }
            }

            override fun onFailure(call: Call<List<petMember>>, t: Throwable) {
                Toast.makeText(context, "โหลดข้อมูลไม่สำเร็จ", Toast.LENGTH_SHORT).show()
            }
        })
    }

    LaunchedEffect(checkInDate, checkOutDate,roomType, days) {
        if (checkInDate != null && checkOutDate != null) {
            val duration = calculateDaysBetween(checkInDate!!, checkOutDate!!)
            days = if (duration > 0) duration else 1
            totalPrice = calculateRoomPrice(roomType, days)
    }

    }

    LaunchedEffect(Unit) {
        name = preferencesManager.userName ?: ""
        email = preferencesManager.email ?: ""
        phone = preferencesManager.tell_number ?: ""
        petTypeName = preferencesManager.petTypeName ?: ""

//        petTypeName = preferencesManager.petTypeName ?: ""
    }
    Log.d("BookingScreen", "petTypeName: $petTypeName")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFFFCEB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ประเภท:$roomType ", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text =  petTypeName.ifEmpty { "ไม่มีข้อมูลประเภทสัตว์เลี้ยง" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

//                Text("05 Dec - 08 Dec", fontSize = 14.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Room Image"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("$days", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ชื่อ-นามสกุล ") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("อีเมล") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("เบอร์โทร") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

//                OutlinedTextField(
//                    value = note,
//                    onValueChange = { note = it },
//                    label = { Text("หมายเหตุ") },
//                    modifier = Modifier.fillMaxWidth()
//                )


                DropDown(selectedPet = selectedPet, onPetSelected = { selectedPet = it }, userId = userId)


            }
        }



        Spacer(modifier = Modifier.height(20.dp))

        Card(

            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            DatePickerScreen(
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
                onCheckInSelected = { checkInDate = it },
                onCheckOutSelected = { checkOutDate = it }
            )


            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Text("ราคาห้องพัก / คืน:")
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "THB ${priceResponse?.payPerNight ?: 0}", // ✅ ป้องกัน null
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                    Text("ราคาห้องพัก $days คืน:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "THB $totalPrice",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.weight(1f)) //  ดันปุ่มชำระเงินลงล่างสุด

        Button(
            onClick = {
                val checkIn = checkInDate?.let { dateFormatter.format(it) } ?: ""
                val checkOut = checkOutDate?.let { dateFormatter.format(it) } ?: ""
                navController.navigate(
                    String.format(
                        "payment_screen/%s/%s/%d",
                        checkIn, checkOut, totalPrice
                    )
                                )
            },

                modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC2B))
        ) {
            Text(

                "ชำระเงิน",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDown(selectedPet: String, onPetSelected: (String) -> Unit, userId: Int) {
    val context = LocalContext.current
    var petList by remember { mutableStateOf(listOf<String>()) }
    var isExpanded by remember { mutableStateOf(false) }

    // ดึงข้อมูลสัตว์เลี้ยงจาก API
    LaunchedEffect(userId) {
        val api = PetApi.create()
        api.mypet(userId).enqueue(object : Callback<List<petMember>> {
            override fun onResponse(call: Call<List<petMember>>, response: Response<List<petMember>>) {
                if (response.isSuccessful) {
                    // แสดงประเภทสัตว์เลี้ยงที่ถูกต้องจากฐานข้อมูล
                    petList = response.body()?.map { it.petName } ?: listOf("ไม่มีสัตว์เลี้ยง")
                }
            }

            override fun onFailure(call: Call<List<petMember>>, t: Throwable) {
                Toast.makeText(context, "โหลดข้อมูลไม่สำเร็จ", Toast.LENGTH_SHORT).show()
            }
        })
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            OutlinedTextField(
                value = selectedPet,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text("สัตว์เลี้ยง") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
                }
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }) {
                petList.forEach { pet ->
                    DropdownMenuItem(
                        text = { Text(pet) },
                        onClick = {
                            onPetSelected(pet)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}





@Composable
fun DatePickerScreen(
    checkInDate: Date?,
    checkOutDate: Date?,
    onCheckInSelected: (Date) -> Unit,
    onCheckOutSelected: (Date) -> Unit
) {

    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    val checkInDatePicker = rememberDatePickerDialog { selectedDate -> onCheckInSelected(selectedDate) }
    val checkOutDatePicker = rememberDatePickerDialog { selectedDate -> onCheckOutSelected(selectedDate) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("เลือกวันที่", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("เช็คอิน", fontSize = 14.sp, color = Color.Gray)
                Button(onClick = { checkInDatePicker.show() },
                    modifier = Modifier,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC2B))
                )
                {

                    Text(checkInDate?.let { dateFormatter.format(it) } ?: "เลือกวันที่")
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("เช็คเอาท์", fontSize = 14.sp, color = Color.Gray)
                Button(onClick = { checkOutDatePicker.show() },
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC2B))) {
                    Text(checkOutDate?.let { dateFormatter.format(it) } ?: "เลือกวันที่")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //  คำนวณจำนวนวันระหว่าง Check-in และ Check-out
        if (checkInDate != null && checkOutDate != null) {
            val durationDays = calculateDaysBetween(checkInDate, checkOutDate)
            Text("จำนวนวันที่เข้าพัก: $durationDays วัน", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ฟังก์ชันเปิด DatePickerDialog และส่งค่ากลับ
@Composable
fun rememberDatePickerDialog(onDateSelected: (Date) -> Unit): DatePickerDialog {
    val context = LocalContext.current
    return DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            onDateSelected(calendar.time)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )
}

// ฟังก์ชันคำนวณจำนวนวันระหว่าง Check-in และ Check-out
fun calculateDaysBetween(startDate: Date, endDate: Date): Int {
    val diff = endDate.time - startDate.time
    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
}
fun calculateRoomPrice(roomType: String, days: Int): Int {
    val pricePerNight = when (roomType) {
        "สแตนดาร์ด" -> 350
        "ดีลักซ์" -> 550
        "วีไอพี" -> 750
        else -> 0
    }
    return pricePerNight * days
}




@Preview(showBackground = true)
@Composable
fun PreviewBookingScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val roomId = 1
    val days = 2
    ASS07Theme {
        BookingScreen(Modifier, navController, context,roomId, days )
    }
}






