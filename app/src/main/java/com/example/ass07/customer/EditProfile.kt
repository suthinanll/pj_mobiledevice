package com.example.ass07.customer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ass07.customer.LoginRegister.SharePreferencesManager

@Composable
fun EditProfile(navController: NavHostController) {
    lateinit var sharePreferences: SharePreferencesManager
    val contextForToast = LocalContext.current.applicationContext
    sharePreferences = SharePreferencesManager(contextForToast)
    val userId = sharePreferences.userId ?: ""
    val userClient = projectApi.create()

    // รับข้อมูลจาก savedStateHandle
    val user = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")
        ?: User(userId = 0, name = "", tellNumber = "", email = "", userType = 0, password = "", avatar = 0)

    // กำหนดค่าตัวแปรตามข้อมูลของ User
    var avatarNumber by rememberSaveable { mutableStateOf(user.avatar.toString()) }
    var username by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var tel by remember { mutableStateOf(user.tellNumber) }

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
                val selectedAvatarResId = contextForToast.resources.getIdentifier(
                    "avatar_$avatarNumber",
                    "drawable",
                    contextForToast.packageName
                )
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
                EditProfileTextField("ชื่อผู้ใช้", username, { username = it }, isPassword = false)
                EditProfileTextField("อีเมล", email, { email = it }, isPassword = false)
                EditProfileTextField("เบอร์โทรศัพท์", tel, { tel = it }, isPassword = false)

                Spacer(modifier = Modifier.height(24.dp))

                // ปุ่ม Save
                Button(
                    onClick = {
                        // ฟังก์ชันบันทึกข้อมูลที่นี่
                        // ตัวอย่าง: อัปเดตข้อมูลผู้ใช้ในฐานข้อมูลหรือแชร์ข้อมูล
                        Toast.makeText(contextForToast, "ข้อมูลบันทึกแล้ว", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()  // กลับไปที่หน้า Profile
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Save", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ปุ่ม Cancel
                Button(
                    onClick = {
                        // กลับไปหน้า Profile โดยไม่บันทึก
                        navController.popBackStack()  // กลับไปที่หน้า Profile
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Cancel", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun EditProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            enabled = true
        )
    }
}
