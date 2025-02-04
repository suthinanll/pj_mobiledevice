package com.example.ass07

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
                    response.body()?.let {
                        petItemsList.addAll(it.filter { pet -> !pet.delete_at })
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
        createClient.softDeletePet(pet.petID.toInt(), currentTime)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(contextForToast, "ลบข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()
                        showAllData()
                    } else {
                        Toast.makeText(contextForToast, "ลบข้อมูลไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(contextForToast, "Error: ${t.message}", Toast.LENGTH_LONG).show()
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
                PetCard(pet, onDelete = { softDeletePet(pet) })
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
fun PetCard(pet: petMember, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("สัตว์เลี้ยงของฉัน", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            Text("ชื่อสัตว์เลี้ยง: ${pet.petName}", fontSize = 16.sp)
            Text("เพศ: ${pet.petGender}", fontSize = 16.sp)
            Text("สายพันธุ์: ${pet.petBreed}", fontSize = 16.sp)
            Text("อายุ: ${pet.petAge} ปี", fontSize = 16.sp)
            Text("น้ำหนัก: ${pet.petWeight} กิโลกรัม", fontSize = 16.sp)
            Text("คำแนะนำ / คำอธิบายเพิ่มเติม: ${pet.additionalInfo}", fontSize = 16.sp)

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { /* แก้ไขข้อมูล */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966))) {
                    Text("แก้ไข", color = Color.Black)
                }
                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966))) {
                    Text("ลบ", color = Color.Black)
                }
            }
        }
    }
}
