package com.example.ass07.customer.LoginRegister

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.example.ass07.R
import com.example.ass07.admin.ScreenAdmin
import com.example.ass07.customer.Screen
import com.example.ass07.customer.API.projectApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Login(navController : NavHostController) {
    val username = navController.previousBackStackEntry?.savedStateHandle?.get<String>("name")
    var password by remember { mutableStateOf("") }
    var accID by remember { mutableStateOf(username ?: "") }
    var isButtonEnabled by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val studentClient = projectApi.create()
    val contextForToast = LocalContext.current.applicationContext

    lateinit var sharePreferences: SharePreferencesManager
    sharePreferences = SharePreferencesManager(contextForToast)

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.DESTROYED -> {}
            Lifecycle.State.INITIALIZED -> {}
            Lifecycle.State.CREATED -> {}
            Lifecycle.State.STARTED -> {}
            Lifecycle.State.RESUMED -> {
                if (sharePreferences.isLoggedIn) {
                    navController.navigate(Screen.Profile.route)
                }

                if (!sharePreferences.email.isNullOrEmpty()) {
                    accID = sharePreferences.email ?: ""
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3D9))
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logoapp),
                    contentDescription = "logo",
                    modifier = Modifier.size(230.dp)
                )

                Text(
                    text = "Login",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                )

                OutlinedTextField(
                    value = accID,
                    onValueChange = {
                        accID = it
                        isButtonEnabled = accID.isNotEmpty() && password.isNotEmpty()
                    },
                    label = { Text("Tell-number/E-mail") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 2.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        isButtonEnabled = accID.isNotEmpty() && password.isNotEmpty()
                    },
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    visualTransformation = PasswordVisualTransformation()
                )

                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()

                        studentClient.login_acc(accID, password)
                            .enqueue(object : Callback<LoginClass> {
                                override fun onResponse(
                                    call: Call<LoginClass>,
                                    response: Response<LoginClass>
                                ) {
                                    if (response.isSuccessful) {
                                        // หลังจากล็อกอินสำเร็จ
                                        val loginResponse = response.body()
                                        if (loginResponse != null && loginResponse.success == 1) {
                                            sharePreferences.isLoggedIn = true
                                            sharePreferences.userId = loginResponse.user_id
                                            sharePreferences.userName = loginResponse.name
                                            sharePreferences.userRole = loginResponse.user_type.toString()  // ✅ บันทึก role ของผู้ใช้
                                            sharePreferences.email = loginResponse.email
                                            sharePreferences.tell_number = loginResponse.tell_number

                                            Toast.makeText(contextForToast, "Login Successful.", Toast.LENGTH_SHORT).show()

                                            // ✅ เช็คว่าเป็น admin หรือ user
                                            if (loginResponse.user_type == 1) {
                                                navController.navigate(ScreenAdmin.ManageRoom.route)  // ไปหน้า Admin
                                            } else {
                                                navController.navigate(Screen.Home.route)  // ไปหน้า User
                                            }

                                    } else {
                                            Toast.makeText(
                                                contextForToast, "Username or password is incorrect.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            contextForToast, "Username not found.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<LoginClass>, t: Throwable) {
                                    Toast.makeText(
                                        contextForToast, "Error onFailure + ${t.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })

                    },
                    enabled = isButtonEnabled,
                    modifier = Modifier.fillMaxWidth().padding(20.dp)
                ) {
                    Text("Login")
                }

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Don't have an account?")
                    TextButton(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            navController.navigate(ScreenLogin.Register.route)
                        }
                    ) {
                        Text("Register")
                    }
                }
            }
        }
    }
    }
