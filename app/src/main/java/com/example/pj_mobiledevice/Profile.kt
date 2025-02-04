package com.example.pj_mobiledevice

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.ass07.R


@Composable
fun Profile() {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var fullName by remember { mutableStateOf("Chill guy") }
    var username by remember { mutableStateOf("chill") }
    var email by remember { mutableStateOf("chillguy@gmail.com") }
    var password by remember { mutableStateOf("************") }

    // State สำหรับควบคุมโหมดแก้ไขของแต่ละช่อง
    val editState = remember { mutableStateMapOf("fullName" to false, "username" to false, "email" to false, "password" to false) }

    var isEditing by remember { mutableStateOf(false) } // ตรวจสอบว่ามีการแก้ไขหรือไม่
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        isEditing = true
    }

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
                // รูปโปรไฟล์
                Box(modifier = Modifier.clickable { launcher.launch("image/*") }) {
                    Image(
                        painter = if (imageUri != null) rememberAsyncImagePainter(imageUri)
                        else painterResource(id = R.drawable.chillguy),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "แตะเพื่อเปลี่ยนรูป", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                // ฟอร์มข้อมูล (แต่ละช่องต้องกดดินสอก่อนถึงจะแก้ไขได้)
                ProfileTextField("ชื่อ-นามสกุล", fullName, editState["fullName"] ?: false, { fullName = it }) {
                    editState["fullName"] = true
                    isEditing = true
                }
                ProfileTextField("ตั้งค่าชื่อผู้ใช้", username, editState["username"] ?: false, { username = it }) {
                    editState["username"] = true
                    isEditing = true
                }
                ProfileTextField("อีเมล", email, editState["email"] ?: false, { email = it }) {
                    editState["email"] = true
                    isEditing = true
                }
                ProfileTextField("รหัสผ่าน", password, editState["password"] ?: false, { password = it }, isPassword = true) {
                    editState["password"] = true
                    isEditing = true
                }

                Spacer(modifier = Modifier.height(24.dp))

                // แสดงปุ่ม "บันทึก" และ "ยกเลิก" เฉพาะเมื่อมีการแก้ไข
                if (isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // ปุ่มยกเลิก
                        Button(
                            onClick = {
                                // รีเซ็ตข้อมูล และปิดโหมดแก้ไข
                                editState.keys.forEach { key -> editState[key] = false }
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text(text = "ยกเลิก", color = Color.White)
                        }

                        // ปุ่มบันทึก
                        Button(
                            onClick = {
                                editState.keys.forEach { key -> editState[key] = false } // ปิดโหมดแก้ไข
                                isEditing = false
                                Toast.makeText(context, "บันทึกข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC33)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "บันทึก", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ปุ่มออกจากระบบ
                Button(
                    onClick = { /* รอ login logout */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
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
}

// ฟังก์ชันสร้างช่องกรอกข้อมูล ไอคอนดินสอ
@Composable
fun ProfileTextField(
    label: String,
    value: String,
    isEditable: Boolean,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    onEditClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = isEditable, // ปิดการแก้ไขหากยังไม่กดปุ่มดินสอ
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { onEditClick() }) { // กดเพื่อเปิดโหมดแก้ไข
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}