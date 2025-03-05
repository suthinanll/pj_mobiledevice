package com.example.ass07.customer.Profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ass07.customer.API.projectApi
import com.example.ass07.customer.LoginRegister.SharePreferencesManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EditProfile(navController: NavHostController) {
    val context = LocalContext.current
    val sharePreferences = remember { SharePreferencesManager(context) }
    val userClient = projectApi.create()

    val userId = sharePreferences.userId ?: 0

    val user = navController.previousBackStackEntry?.savedStateHandle?.get<User>("user")
        ?: User(userId = 0, name = "", tellNumber = "", email = "", userType = 0, password = "", avatar = 0)

    var avatarNumber by rememberSaveable { mutableStateOf(user.avatar.toString()) }
    var username by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var tel by remember { mutableStateOf(user.tellNumber) }
    var isLoading by remember { mutableStateOf(false) }
    var isAvatarDialogOpen by remember { mutableStateOf(false) }

    fun updateProfile() {
        if (username.isBlank() || email.isBlank() || tel.isBlank()) {
            Toast.makeText(context, "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true

        val updateData = UpdateProfileRequest(
            name = username,
            email = email,
            tell_number = tel,
            avatar = avatarNumber.toInt()
        )

        val call = userClient.updateProfile(user.userId, updateData)
        call.enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    sharePreferences.userName = username
                    sharePreferences.email = email
                    sharePreferences.tell_number = tel
                    Toast.makeText(context, "บันทึกการแก้ไขสำเร็จ", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    // รับ message จาก response body
                    val errorMessage = response.errorBody()?.string()
                    // สมมุติว่า response เป็น JSON ที่มี key "message"
                    try {
                        val jsonObject = JSONObject(errorMessage)
                        val message = jsonObject.optString("message", "เกิดข้อผิดพลาดในการอัปเดต")
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "เกิดข้อผิดพลาดในการอัปเดต", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                isLoading = false
                Toast.makeText(context, "เกิดข้อผิดพลาด: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun showAvatarSelectionDialog() {
        isAvatarDialogOpen = true
    }

    fun onAvatarSelected(avatar: Int) {
        avatarNumber = avatar.toString()
        isAvatarDialogOpen = false
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ภาพโปรไฟล์ของคุณ", fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                val selectedAvatarResId = context.resources.getIdentifier(
                    "avatar_$avatarNumber", "drawable", context.packageName
                )
                if (selectedAvatarResId != 0) {
                    Image(
                        painter = painterResource(id = selectedAvatarResId),
                        contentDescription = "Selected Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                            .clickable { showAvatarSelectionDialog() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                EditProfileTextField("ชื่อผู้ใช้", username, { username = it })
                EditProfileTextField("อีเมล", email, { email = it })
                EditProfileTextField("เบอร์โทรศัพท์", tel, { tel = it })

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { updateProfile() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading
                ) {
                    Text(text = if (isLoading) "กำลังบันทึก..." else "Save", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(text = "Cancel", color = Color.White)
                }
            }
        }
    }

    if (isAvatarDialogOpen) {
        AvatarSelectionDialog(
            onAvatarSelected = { avatar -> onAvatarSelected(avatar) },
            currentAvatar = avatarNumber.toInt() // ส่งค่า avatar ปัจจุบันให้ Dialog
        )
    }
}

@Composable
fun AvatarSelectionDialog(
    onAvatarSelected: (Int) -> Unit,
    currentAvatar: Int
) {
    AlertDialog(
        onDismissRequest = { onAvatarSelected(currentAvatar) },
        title = { Text("เลือก Avatar") },
        text = {
            Column {
                for (i in 0..9) {
                    val avatarResId = LocalContext.current.resources.getIdentifier(
                        "avatar_$i", "drawable", LocalContext.current.packageName
                    )
                    if (avatarResId != 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { onAvatarSelected(i) }
                        ) {
                            Image(
                                painter = painterResource(id = avatarResId),
                                contentDescription = "Avatar $i",
                                modifier = Modifier.size(40.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = "Avatar $i", modifier = Modifier.align(Alignment.CenterVertically))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onAvatarSelected(currentAvatar) }) {
                Text("ยกเลิก")
            }
        }
    )
}

@Composable
fun EditProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        )
    }
}