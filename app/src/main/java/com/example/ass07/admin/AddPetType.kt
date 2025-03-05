package com.example.ass07.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ass07.customer.Mypet.AddPetTypeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AddPetType(navController: NavHostController) {
    var newPetTypeName by remember { mutableStateOf("") }
    val contextForToast = LocalContext.current
    val createClient = PetApi.create()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAF0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth()
                ){
                    IconButton(
                        onClick = { navController.navigate(ScreenAdmin.PetsAdmin.route)} // Navigate back on click
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = "เพิ่มประเภทสัตว์เลี้ยง",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )


                OutlinedTextField(
                    value = newPetTypeName,
                    onValueChange = { newPetTypeName = it },
                    label = { Text("ชื่อประเภทสัตว์") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (newPetTypeName.isNotBlank()) {
                            createClient.addPetType(newPetTypeName).enqueue(object : Callback<AddPetTypeResponse> {
                                override fun onResponse(call: Call<AddPetTypeResponse>, response: Response<AddPetTypeResponse>) {
                                    if (response.isSuccessful && response.body()?.error == false) {
                                        Toast.makeText(contextForToast, "เพิ่มประเภทสัตว์สำเร็จ", Toast.LENGTH_SHORT).show()
                                        navController.navigate(ScreenAdmin.PetsAdmin.route)
                                    } else {
                                        Toast.makeText(
                                            contextForToast,
                                            "เพิ่มประเภทสัตว์ไม่สำเร็จ: ${response.body()?.message ?: response.message()}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onFailure(call: Call<AddPetTypeResponse>, t: Throwable) {
                                    Log.e("API_ERROR", "Error: ${newPetTypeName}")
                                    Toast.makeText(contextForToast, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_LONG).show()
                                }
                            })
                        } else {
                            Toast.makeText(contextForToast, "กรุณากรอกชื่อประเภทสัตว์", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("เพิ่มประเภทสัตว์", color = Color.Black)
                }
            }
        }
    }
}
