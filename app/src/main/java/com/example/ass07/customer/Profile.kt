package com.example.ass07.customer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ass07.customer.API.projectApi
import com.example.ass07.customer.LoginRegister.ScreenLogin
import com.example.ass07.customer.LoginRegister.SharePreferencesManager

@Composable
fun Profile(navController : NavHostController){
    lateinit var sharePreferences : SharePreferencesManager
    val contextForToast = LocalContext.current.applicationContext
    sharePreferences = SharePreferencesManager(contextForToast)

    val userId = sharePreferences.userId ?: 0
    val studentClient = projectApi.create()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    var remember_id by remember { mutableStateOf(false) }
    var alertDialog by remember { mutableStateOf(false) }


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Profile",
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.height(20.dp))



        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                alertDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 16.dp) //  เพิ่มระยะขอบด้านข้าง
                .shadow(8.dp, shape = RoundedCornerShape(12.dp)), // เพิ่มเงาและโค้งมน
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Logout")
        }
    }

    if(alertDialog){
        AlertDialog(
            onDismissRequest = {alertDialog = false},
            title = { Text("Logout") },
            text = {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ){
                    Text("Do you want to logout?")
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ){
                        Checkbox(
                            checked = remember_id,
                            onCheckedChange = {
                                remember_id = !remember_id
                            },
                            modifier = Modifier.padding(end = 10.dp)
                        )

                        Text("Remember my username ${sharePreferences.userName}")
                    }
                }
            },
            confirmButton ={
                TextButton(
                    onClick = {
                        if(remember_id){
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "name",sharePreferences.userName
                            )
                        }
                        sharePreferences.isLoggedIn = false
                        sharePreferences.userId = 0
                        navController.navigate(ScreenLogin.Login.route)
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        alertDialog = false
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}