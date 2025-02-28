
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ass07.R
import com.example.ass07.admin.Room
import com.example.ass07.customer.API.PetApi
import com.example.ass07.customer.API.projectApi
import com.example.ass07.customer.BB
import com.example.ass07.customer.BB.Companion.MyScaffoldLayout
import com.example.ass07.customer.BookingClass
import com.example.ass07.customer.LoginRegister.SharePreferencesManager
import com.example.ass07.customer.Mypet.PetViewModel
import com.example.ass07.customer.Mypet.petMember
import com.example.ass07.customer.Profile.User
import com.example.ass07.customer.Screen
import com.example.ass07.customer.convertDateToMonthName
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
    navController: NavHostController
) {
    val bookingData = navController.previousBackStackEntry?.savedStateHandle?.get<BookingClass>("booking_data")
    val totalPrice = navController.previousBackStackEntry?.savedStateHandle?.get<Double>("total_price") ?: 0.0
    val days = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("days") ?: 1

    val context = LocalContext.current

    var roomType by remember { mutableStateOf("สแตนดาร์ด") }
    val preferencesManager = remember { SharePreferencesManager(context) }

    var name by remember { mutableStateOf(preferencesManager.userName ?: "") }
    var email by remember { mutableStateOf(preferencesManager.email ?: "") }
    var phone by remember { mutableStateOf(preferencesManager.tell_number ?: "") }

    var checkInDate by remember { mutableStateOf<Date?>(null) }
    var checkOutDate by remember { mutableStateOf<Date?>(null) }

    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val checkInFormatted = checkInDate?.let { dateFormatter.format(it) } ?: ""
    val checkOutFormatted = checkOutDate?.let { dateFormatter.format(it) } ?: ""

    val userId = preferencesManager.userId ?: 0
    var selectedPet by remember { mutableStateOf("") }

    val room = navController.previousBackStackEntry?.savedStateHandle?.get<Room>("room_data")
    val checkin = navController.previousBackStackEntry?.savedStateHandle?.get<String>("checkin")
    val checkout  = navController.previousBackStackEntry?.savedStateHandle?.get<String>("checkout")
    val pet = navController.previousBackStackEntry?.savedStateHandle?.get<Int>("pet")

    val formattedCheckIn = convertDateToMonthName(checkin ?: "")
    val formattedCheckOut = convertDateToMonthName(checkout ?: "")

    val petTypeName = when (pet ?: 0) {
        1 -> "สุนัข"
        2 -> "แมว"
        3 -> "นก"
        else -> "ไม่ทราบ"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFFFCEB))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BookingHeader(
            room?.name_type ?: "",
            bookingData?.petType ?: "ไม่ทราบ",
            bookingData?.checkInDate ?: "",
            bookingData?.checkOutDate ?: "",
        )

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
                        Text("$days วัน", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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

                DropDown(selectedPet = selectedPet, onPetSelected = { selectedPet = it }, userId = userId)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Text("ราคาห้องพัก $days วัน:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "THB ${totalPrice}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val checkIn = checkInDate?.let { dateFormatter.format(it) } ?: ""
                val checkOut = checkOutDate?.let { dateFormatter.format(it) } ?: ""
                val days = days?.let { it } ?: 1


                navController.currentBackStackEntry?.savedStateHandle?.set("checkIn", bookingData?.checkInDate ?: "")
                navController.currentBackStackEntry?.savedStateHandle?.set("checkOut", bookingData?.checkOutDate ?: "")
                navController.currentBackStackEntry?.savedStateHandle?.set("days", days)
                navController.currentBackStackEntry?.savedStateHandle?.set("petType", bookingData?.petType ?: "")

                navController.currentBackStackEntry?.savedStateHandle?.set("totalPrice", totalPrice)

                // นำทางไปยัง PaymentScreen
                navController.navigate("payment_screen")

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFBC2B))
        ) {
            Text(
                "ยืนยันการจอง",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BookingHeader(roomType: String, petType: String, checkIn: String, checkOut: String) {
    Spacer(modifier = Modifier.height(50.dp))
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ประเภท: $petType", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "$checkIn - $checkOut", fontSize = 14.sp, color = Color.Gray)

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
fun BookingButton(
    navController: NavHostController,
    roomId: String,
    roomType: String,
    checkInDate: String,
    checkOutDate: String,
    petType: String,
    price: String
) {
    Button(
        onClick = {
            // ส่งข้อมูลไปหน้าจองห้องพัก

            val bookingInfoRoute = "bookingInfo?roomId=$roomId&roomType=$roomType&checkIn=$checkInDate&checkOut=$checkOutDate&petType=$petType&price=$price"
            navController.navigate(bookingInfoRoute)
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

// ให้เพิ่มฟังก์ชัน convertDateToMonthName ด้วย (คัดลอกมาจากหน้า Search)
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








