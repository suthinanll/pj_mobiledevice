package com.example.ass07.customer.Profile

import android.content.Context
import android.util.Log
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ass07.customer.Screen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Profile(navController: NavHostController) {
    lateinit var sharePreferences: SharePreferencesManager
    val contextForToast = LocalContext.current.applicationContext
    sharePreferences = SharePreferencesManager(contextForToast)

    val userId = sharePreferences.userId ?: 0
    // ตรวจสอบว่า projectApi.create() ถูกสร้างถูกต้องไหม
    val userClient = projectApi.create()


    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    var alertDialog by remember { mutableStateOf(false) }
    var rememberId by remember { mutableStateOf(false) }

    var userProfile by remember {
        mutableStateOf(
            User(
            userId = 0,
            name = "",
            email = "",
            tellNumber = "",
            userType = 0,
            password = "",
            avatar = 1
        )
        )
    }

    // ฟังก์ชันเรียกข้อมูลโปรไฟล์
    LaunchedEffect(userId) {
        Log.d("Profile", "Requesting profile for userId: $userId") // เพิ่ม log เพื่อดู userId ที่ส่งไป

        if (userId != 0) {
            userClient.getProfileByID(userId).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            userProfile = user
                            Log.d("Profile", "Profile loaded successfully: ${user.name}")
                        }
                    } else {
                        when (response.code()) {
                            404 -> {
                                Log.e("Profile", "User not found for ID: $userId")
                                Toast.makeText(
                                    contextForToast,
                                    "ไม่พบข้อมูลผู้ใช้",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else -> {
                                Log.e("Profile", "Server error: ${response.code()}")
                                Toast.makeText(
                                    contextForToast,
                                    "เกิดข้อผิดพลาดในการโหลดข้อมูล",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("Profile", "Network Error: ", t)
                    Toast.makeText(
                        contextForToast,
                        "ไม่สามารถเชื่อมต่อกับเซิร์ฟเวอร์ได้",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileContent(
            user = userProfile,
            contextForToast = contextForToast,
            onEditClick = {
                navController.currentBackStackEntry?.savedStateHandle?.set("user", userProfile)
                navController.navigate(route = Screen.EditProfile.route)
            },
            onLogoutClick = {
                alertDialog = true
            }
        )
    }

    // แสดง AlertDialog เมื่อคลิก Logout (คงไว้เหมือนเดิม)
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
                        Text("Remember my username ${sharePreferences.userName}")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (rememberId) {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "name",sharePreferences.userName
                            )}
                        sharePreferences.isLoggedIn = false
                        sharePreferences.userId = 0
                        navController.navigate(ScreenLogin.Login.route)
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

@Composable
private fun ProfileContent(
    user: User,
    contextForToast: Context,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
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
            ProfileAvatar(avatarNumber = user.avatar.toString(), context = contextForToast)
            ProfileInformation(user = user)
            ProfileActions(onEditClick = onEditClick, onLogoutClick = onLogoutClick)
        }
    }
}

@Composable
private fun ProfileAvatar(avatarNumber: String, context: Context) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "ภาพโปรไฟล์ของคุณ", fontSize = 14.sp, color = Color.Gray)

    Spacer(modifier = Modifier.height(16.dp))

    val selectedAvatarResId = context.resources.getIdentifier(
        "avatar_$avatarNumber",
        "drawable",
        context.packageName
    )
    if (selectedAvatarResId != 0) {
        Image(
            painter = painterResource(id = selectedAvatarResId),
            contentDescription = "Selected Avatar",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ProfileInformation(user: User) {
    ProfileTextField("ชื่อผู้ใช้", user.name, {})
    ProfileTextField("อีเมล", user.email, {})
    ProfileTextField("เบอร์โทรศัพท์", user.tellNumber, {})
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun ProfileActions(onEditClick: () -> Unit, onLogoutClick: () -> Unit) {
    Button(
        onClick = onEditClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Color(255, 188, 43, 255)
        )
    ) {
        Text(text = "แก้ไขโปรไฟล์", color = Color.White)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onLogoutClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(Color.Red)

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

@Composable
fun ProfileTextField(
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
            enabled = false
        )
    }
}

