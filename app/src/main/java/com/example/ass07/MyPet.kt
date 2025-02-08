package com.example.ass07

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyPet(navController: NavHostController) {
    var petItemsList = remember { mutableStateListOf<petMember>() }
    val contextForToast = LocalContext.current.applicationContext




    fun showAllData() {
        val createClient = PetApi.create()
        createClient.retrievepetMember()
            .enqueue(object : Callback<List<petMember>> {
                override fun onResponse(
                    call: Call<List<petMember>>, response: Response<List<petMember>>
                ) {
                    petItemsList.clear()

                    if (response.isSuccessful) {
                        val pets = response.body()
                        pets?.let {
                            val filteredPets = it.filter { pet -> pet.deleted_at.isNullOrEmpty() }
                            petItemsList.addAll(filteredPets)

                        }
                    } else {
                        Log.e("API_ERROR", "Response failed: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<petMember>>, t: Throwable) {
                    Toast.makeText(contextForToast, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })

    }



    fun softDeletePet(pet: petMember) {
        val createClient = PetApi.create()
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        Log.d("SoftDelete", "Deleting pet ID: ${pet.petID}, Time: $currentTime") // ✅ Debug Log

        createClient.softDeletePet(pet.petID.toInt(), currentTime)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("SoftDelete", "Response code: ${response.code()}") // ✅ Debug Response Code
                    if (response.isSuccessful) {
                        Toast.makeText(contextForToast, "ลบข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()
                        showAllData()
                    } else {
                        Toast.makeText(contextForToast, "ลบข้อมูลไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                        Log.e("SoftDelete", "Error: ${response.errorBody()?.string()}") // ✅ Debug Error
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(contextForToast, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("SoftDelete", "Failure: ${t.message}") // ✅ Debug Failure
                }
            })
    }


    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle.currentState) {
        if (lifecycleOwner.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
            showAllData()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFAF0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(petItemsList) { _, pet ->
                PetCard(pet, onDelete = { softDeletePet(pet) },navController)
            }
        }


        Button(
            onClick = { navController.navigate(Screen.Mypetinsert.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("เพิ่มข้อมูลสัตว์เลี้ยง", fontSize = 18.sp, color = Color.Black)
        }
    }
}


@Composable
fun PetCard(pet: petMember, onDelete: () -> Unit ,navController: NavHostController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("สัตว์เลี้ยงของฉัน", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            Text("ชื่อสัตว์เลี้ยง: ${pet.petName}", fontSize = 16.sp)
            Text("ประเภท: ${pet.petTypename}", fontSize = 16.sp)
            Text(
                "เพศ: ${if (pet.petGender == "M") "เพศผู้" else if (pet.petGender == "F") "เพศเมีย" else "ไม่ระบุ"}",
                fontSize = 16.sp
            )
            Text("สายพันธุ์: ${pet.petBreed}", fontSize = 16.sp)
            Text("อายุ: ${pet.petAge} ปี", fontSize = 16.sp)
            Text("น้ำหนัก: ${pet.petWeight} กิโลกรัม", fontSize = 16.sp)
            Text("คำแนะนำ / คำอธิบายเพิ่มเติม: ${pet.additionalInfo}", fontSize = 16.sp)

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { navController.navigate("mypetedit/${pet.petID}") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966))
                ) {
                    Text("แก้ไข", color = Color.Black)
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966))
                ) {
                    Text("ลบ", color = Color.Black)
                }
            }
        }
    }
}


