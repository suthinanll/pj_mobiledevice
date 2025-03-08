package com.example.ass07.customer

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.ass07.R
import com.example.ass07.admin.RoomAPI
import com.example.ass07.customer.LoginRegister.SharePreferencesManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


@SuppressLint("SimpleDateFormat")
@Composable
fun History() {
    val scrollState = rememberScrollState()

    val paymentList = listOf("บัตรเครดิต","ชำระผ่าน mobile banking","พร้อมเพย์","เคาท์เตอร์เซอร์วิส")
    var selectedPayment by remember { mutableStateOf("") }

    var bookingData by remember { mutableStateOf<List<BookingData>>(emptyList()) }

    val roomClient = RoomAPI.create()

    val context = LocalContext.current
    val sharePreferences = remember { SharePreferencesManager(context) }

    var paymentAlert by remember { mutableStateOf(false) }
    var selectedBooking by remember { mutableIntStateOf(0) }
    var selectedTotalPrice by remember { mutableIntStateOf(0) }


    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> {
                roomClient.getBooking(sharePreferences.userId!!).enqueue(object : Callback<List<BookingData>>{
                    override fun onResponse(
                        call: Call<List<BookingData>>,
                        response: Response<List<BookingData>>
                    ) {
                        if (response.isSuccessful) {bookingData = response.body() ?: emptyList()}
                        else{
                            Toast.makeText(context,"Data not found",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<List<BookingData>>, t: Throwable) {
                        Toast.makeText(context,"Error onFailure",Toast.LENGTH_SHORT).show()
                        Log.e("Error",t.message ?: "No Message")
                    }
                } )
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        bookingData.forEach { booking->
            var paymentMethod by remember { mutableStateOf<PaymentMethodData?>(null) }
            var pet by remember { mutableStateOf<PetData?>(null) }
            var room by remember { mutableStateOf<RoomData?>(null) }
            var roomType by remember { mutableStateOf<RoomTypeData?>(null) }

            roomClient.getPaymentMethod(booking.paymentMethod)
                .enqueue(object : Callback<PaymentMethodData>{
                    override fun onResponse(
                        call: Call<PaymentMethodData>,
                        response: Response<PaymentMethodData>
                    ) {
                        if(response.isSuccessful){
                            paymentMethod = response.body()
                        }else{
                            Toast.makeText(context,"Data not found",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<PaymentMethodData>, t: Throwable) {
                        Toast.makeText(context,"Error onFailure",Toast.LENGTH_SHORT).show()
                        Log.e("Error",t.message ?: "No Message")
                    }
                })

            roomClient.getPet(booking.petId)
                .enqueue(object : Callback<PetData>{
                    override fun onResponse(call: Call<PetData>, response: Response<PetData>) {
                        if(response.isSuccessful){
                            pet = response.body()
                        }else{
                            Toast.makeText(context,"Data not found",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<PetData>, t: Throwable) {
                        Toast.makeText(context,"Error onFailure",Toast.LENGTH_SHORT).show()
                        Log.e("Error",t.message ?: "No Message")
                    }
                })

            roomClient.getRoom(booking.roomId)
                .enqueue(object : Callback<RoomData>{
                    override fun onResponse(call: Call<RoomData>, response: Response<RoomData>) {
                        if(response.isSuccessful){
                            room = response.body()
                        }else{
                            Toast.makeText(context,"Data not found",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RoomData>, t: Throwable) {
                        Toast.makeText(context,"Error onFailure",Toast.LENGTH_SHORT).show()
                        Log.e("Error",t.message ?: "No Message")
                    }
                })

            if(room != null){
                roomClient.getRoomType(room?.typeTypeId ?: 0)
                    .enqueue(object : Callback<RoomTypeData>{
                        override fun onResponse(
                            call: Call<RoomTypeData>,
                            response: Response<RoomTypeData>
                        ) {
                            if(response.isSuccessful){
                                roomType = response.body()
                            }else{
                                Toast.makeText(context,"Data not found",Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RoomTypeData>, t: Throwable) {
                            Toast.makeText(context,"Error onFailure",Toast.LENGTH_SHORT).show()
                            Log.e("Error",t.message ?: "No Message")
                        }
                    })
            }

            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")

            val createdAtString = booking.createdAt?.toString()

            val createdAt = createdAtString?.let {
                val createdAtFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                createdAtFormat.parse(it)
            }

            val formattedCreated = createdAt?.let {
                val outputCreatedAtFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                outputCreatedAtFormat.format(it)
            } ?: "Unknown Date"


            val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)

            val checkInString = booking.checkIn
            val checkOutString = booking.checkOut

            val checkInDate = checkInString.let { isoFormat.parse(it) }
            val checkOutDate = checkOutString.let { isoFormat.parse(it) }

            val formattedCheckInDate = checkInDate?.let { outputDateFormat.format(it) }
            val formattedCheckInTime = checkInDate?.let { outputTimeFormat.format(it) }

            val formattedCheckOutDate = checkOutDate?.let { outputDateFormat.format(it) }
            val formattedCheckOutTime = checkOutDate?.let { outputTimeFormat.format(it) }

            val diffInDays = if (checkInDate != null && checkOutDate != null) {
                val diffInMillis = checkOutDate.time - checkInDate.time
                TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
            } else {
                null
            }

            var isPaid by remember { mutableStateOf(booking.totalPay == 0) }
            var isExtend by remember { mutableStateOf(booking.adjust != 0 && isPaid || !isPaid) }
            val totalPrice = booking.pay + (booking.adjust ?: 0)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Text(
                        text = "รายละเอียดห้องพัก",
//                        text = "รายละเอียดห้องพัก ${room?.roomId}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        if(roomType?.image != null){
                            Image(
                                painter = rememberAsyncImagePainter(
                                    roomType?.image?.replace("uploads\\","http://10.0.2.2:3000/uploads/")
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Fit
                            )
                        }else{
                            Image(
                                painter = painterResource(R.drawable.room_standard),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Column (
                            horizontalAlignment = Alignment.Start
                        ){
                            Text(
                                text = "1 x ${roomType?.nameType}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "8 ตารางเมตร | ห้องขนาดใหญ่ | อากาศถ่ายเท",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.LightGray
                            )
                        }
                    }

                    HorizontalDivider(thickness = 0.5.dp)

                    Text(
                        text = "รายละเอียดการจอง",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "ชื่อผู้จอง : ${sharePreferences.userName}",
                                fontSize = 14.sp
                            )

                            Text(
                                text = "ชื่อผู้เข้าพัก : ${pet?.petName}",
                                fontSize = 14.sp
                            )

                            Text(
                                text = "วันที่จอง : $formattedCreated",
                                fontSize = 14.sp
                            )
                        }
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = "เช็คอิน",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.LightGray
                            )

                            Text(
                                text = "เช็คเอาท์",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.LightGray
                            )
                        }

                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = formattedCheckInDate ?: "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )

                            Text(
                                text = formattedCheckOutDate ?: "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = formattedCheckInTime ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.LightGray
                            )

                            Text(
                                text = formattedCheckOutTime ?: "",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.LightGray
                            )
                        }
                    }

                    HorizontalDivider(thickness = 0.5.dp)


                    Text(
                        text = "ข้อมูลการชำระเงิน",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Column (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = "1 ห้อง $diffInDays คืน",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "THB $totalPrice",
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        if(isExtend){
                            Row (
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                Text(
                                    text = "ค่าใช้จ่ายเพิ่มเติม\n[ขยายเวลา]",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                Text(
                                    text = "THB ${booking.adjust}",
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = "ภาษีค่าธรรมเนียม",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "THB 0",
                                fontSize = 16.sp
                            )
                        }

                    }

                    HorizontalDivider(thickness = 0.5.dp)

                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            "ราคารวมทั้งสิ้น",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "THB ${totalPrice}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    HorizontalDivider(thickness = 0.5.dp)

                    if(isPaid){
                        Text(
                            text = "ช่องทางการชำระเงิน",
                            fontSize = 14.sp,
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Column (
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ){
                            paymentList.forEach { payment->
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ){
                                    RadioButton(
                                        selected = (payment == selectedPayment),
                                        onClick = {
                                            selectedPayment = payment
                                        }
                                    )

                                    Text(
                                        text = payment,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            selectedBooking = booking.bookingId
                            selectedTotalPrice = totalPrice
                            paymentAlert = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(255, 188, 43, 255)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isPaid
                    ) {
                        Text(
                            text = if(isPaid) "ชำระเงิน"
                            else "ชำระแล้ว",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

        }
    }

    if(paymentAlert){
        AlertDialog(
            onDismissRequest = {
                paymentAlert = false
            },
            title = {
                Text("ชำระเงิน")
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.qr_code),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "ยอดชำระ $selectedTotalPrice บาท",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        roomClient.updateBookingStatus(selectedBooking)
                            .enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(
                                    call: Call<ResponseBody>,
                                    response: Response<ResponseBody>
                                ) {
                                    if(response.isSuccessful){
                                        Toast.makeText(context,"Payment Success",Toast.LENGTH_SHORT).show()
                                    }else{
                                        Log.e("Error",response.message())
                                        Toast.makeText(context,"Payment Failed",Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    Toast.makeText(context,"Error onFailure",Toast.LENGTH_SHORT).show()
                                    Log.e("Error",t.message ?: "No Message")
                                }
                            })

                        paymentAlert = false
                    }
                ) {
                    Text("ตกลง")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        paymentAlert = false
                    }
                ) {
                    Text("ยกเลิก")
                }
            }
        )
    }
}