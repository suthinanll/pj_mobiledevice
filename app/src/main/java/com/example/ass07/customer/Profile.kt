package com.example.ass07.customer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ass07.customer.API.projectApi
import com.example.ass07.customer.LoginRegister.ScreenLogin
import com.example.ass07.customer.LoginRegister.SharePreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Profile(navController: NavHostController) {
    lateinit var sharePreferences: SharePreferencesManager
    val contextForToast = LocalContext.current.applicationContext
    sharePreferences = SharePreferencesManager(contextForToast)

    val userId = sharePreferences.userId ?: ""
    val userClient = projectApi.create()

    var id by remember { mutableIntStateOf(0) }
    var avatarNumber by rememberSaveable { mutableStateOf<String>("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var tel by remember { mutableStateOf("") }
    var usertype by remember { mutableIntStateOf(0) }

    // สถานะของ AlertDialog
    var alertDialog by remember { mutableStateOf(false) }
    var rememberId by remember { mutableStateOf(false) }  // สำหรับเช็คว่าจำรหัสนักศึกษาหรือไม่

    // ฟังก์ชันเรียกข้อมูลโปรไฟล์
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            userClient.getProfileByID(userId).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        user?.let {
                            id = it.userId
                            username = it.name
                            email = it.email
                            tel = it.tellNumber
                            avatarNumber = it.avatar.toString()
                            usertype = it.userType
                        }
                    } else {
                        Toast.makeText(contextForToast, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(contextForToast, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // UI ส่วนแสดงโปรไฟล์
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ภาพโปรไฟล์ของคุณ", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                // แสดง avatar ที่เลือก
                val selectedAvatarResId = contextForToast.resources.getIdentifier("avatar_$avatarNumber", "drawable", contextForToast.packageName)
                if (selectedAvatarResId != 0) {
                    Image(
                        painter = painterResource(id = selectedAvatarResId),
                        contentDescription = "Selected Avatar",
                        modifier = Modifier
                            .size(100.dp)  // ขนาดของภาพ
                            .clip(CircleShape)  // ทำให้เป็นวงกลม
                            .border(2.dp, Color.Gray, CircleShape)  // กรอบวงกลมสีเทา ขนาด 2 dp
                    )

                }

                Spacer(modifier = Modifier.height(16.dp))

                // แสดงข้อมูลโปรไฟล์
                ProfileTextField("ชื่อผู้ใช้", username, { username = it }, isPassword = false)
                ProfileTextField("อีเมล", email, { email = it }, isPassword = false)
                ProfileTextField("เบอร์โทรศัพท์",tel,{ tel = it},isPassword = false)
                Spacer(modifier = Modifier.height(24.dp))

                // ปุ่มแก้ไขโปรไฟล์
                Button(
                    onClick = {
                        // ส่งข้อมูลไปยังหน้า EditProfile
                        navController.currentBackStackEntry?.savedStateHandle?.set("user", User(
                            userId = id,
                            name = username,
                            tellNumber = tel,
                            email = email,
                            userType = usertype,
                            password = "",
                            avatar = avatarNumber.toInt()  // แก้ไขตรงนี้
                        ))
                        navController.navigate(route = Screen.EditProfile.route)  // ไปหน้า EditProfile
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "แก้ไขโปรไฟล์", color = Color.White)
                }


                Spacer(modifier = Modifier.height(16.dp))

                // ปุ่ม Logout
                Button(
                    onClick = {
                        alertDialog = true  // เปิด AlertDialog
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ออกจากระบบ", color = Color.White)
                }
            }
        }
    }

    // แสดง AlertDialog เมื่อคลิก Logout
    if (alertDialog) {
        AlertDialog(
            onDismissRequest = { alertDialog = false },
            title = { Text("Logout") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text("Do you want to logout?")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Checkbox(
                            checked = rememberId,
                            onCheckedChange = { rememberId = !rememberId },
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Text("Remember my student id")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // ถ้าจำรหัสนักศึกษาจะบันทึก userId ใน savedStateHandle
                        if (rememberId) {
                            navController.currentBackStackEntry?.savedStateHandle?.set("name", sharePreferences.userId)
                        }
                        // ล้างข้อมูลการเข้าสู่ระบบ
                        sharePreferences.isLoggedIn = false
                        sharePreferences.userId = ""
                        navController.navigate(ScreenLogin.Login.route)  // ไปหน้าล็อกอิน
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { alertDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

// ฟังก์ชันสร้างช่องกรอกข้อมูล
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Black,fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            enabled = false // ปิดการแก้ไข
        )
    }
}


