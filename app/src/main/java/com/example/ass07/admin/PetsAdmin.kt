package com.example.ass07.admin

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.example.ass07.customer.API.PetApi
import com.example.ass07.customer.Mypet.PetCard
import com.example.ass07.customer.Mypet.petMember
import com.example.ass07.customer.Screen
import com.example.ass07.customer.Mypet.PetType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PetsAdmin(navController: NavHostController){
    val contextForToast = LocalContext.current
    var petItemsList = remember { mutableStateListOf<petMember>() }
    var petType = remember { mutableStateListOf<PetType>() }
    var selectedPetType by remember { mutableStateOf<PetType?>(null) } // สถานะการเลือกประเภทสัตว์เลี้ยง
    var expanded by remember { mutableStateOf(false) }

    fun getdata() {
        val createClient = PetApi.create()
        createClient.allpet()
            .enqueue(object : Callback<List<petMember>> {
                override fun onResponse(
                    call: Call<List<petMember>>, response: Response<List<petMember>>
                ) {
                    petItemsList.clear()

                    if (response.isSuccessful) {
                        val pets = response.body()
                        pets?.let {
                            val filteredPets = it.filter { pet -> pet.deleted_at.isNullOrEmpty() }
                            val filteredByPetType = if (selectedPetType != null) {
                                filteredPets.filter { pet -> pet.petTypename == selectedPetType?.Pet_name_type }
                            } else {
                                filteredPets
                            }
                            petItemsList.addAll(filteredByPetType)
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

    fun getPetTypes() {
        val createClient = PetApi.create()
        createClient.getPetTypes()
            .enqueue(object : Callback<List<PetType>> {
                override fun onResponse(call: Call<List<PetType>>, response: Response<List<PetType>>) {
                    if (response.isSuccessful) {
                        val petTypesResponse = response.body()
                        petTypesResponse?.let {
                            petType.clear()
                            petType.addAll(it)
                        }
                        Log.d("DEBUG_PETTYPE", "TYPE: $petType")
                    } else {
                        Log.e("API_ERROR", "Failed to load pet types: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<PetType>>, t: Throwable) {
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
                        getdata()
                    } else {
                        Toast.makeText(contextForToast, "ลบข้อมูลไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                        Log.e("SoftDelete", "Error: ${response.errorBody()?.string()}") // ✅ Debug Error
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(contextForToast, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    Log.e("SoftDelete", "Failure: ${t.message}")
                }
            })
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle.currentState) {
        getdata()
        getPetTypes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFAF0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "🐾 สัตว์เลี้ยงทั้งหมด 🐶🐱",
            fontSize = 24.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 15.dp),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = selectedPetType?.Pet_name_type ?: "เลือกประเภท",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Icon",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                petType.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.Pet_name_type) },
                        onClick = {
                            selectedPetType = type
                            getdata()
                            expanded = false
                        }
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(petItemsList) { _, pet ->
                PetCard2(pet, onDelete = { softDeletePet(pet) },navController)
            }

        }
    }



//
////        Image(
////            painter = painterResource(id = R.drawable.img_8229),
////            contentDescription = "me",
////            modifier = Modifier.size(300.dp)
////        )
//
//        Text(
//            text = "Pets",
//            fontSize = 25.sp,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        )
//    }
}


@Composable
fun PetCard2(pet: petMember, onDelete: () -> Unit, navController: NavHostController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(25.dp)) {
            Text("🐾 ชื่อสัตว์เลี้ยง: ${pet.petName}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966))
                ) {
                    Text("แก้ไข", color = Color.Black)
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xD7EE2C2C))
                ) {
                    Text("ลบ", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PetTypeDropdownMenu(
    petTypes: List<PetType>,
    selectedPetType: PetType?,
    onPetTypeSelected: (PetType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
//        Button(
//            onClick = { expanded = true },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = selectedPetType?.Pet_name_type ?: "เลือกประเภทสัตว์เลี้ยง")
//        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End // จัดปุ่มไปทางขวา
        ) {
            OutlinedButton(
                onClick = { expanded = true },
                shape = RoundedCornerShape(8.dp), // ปรับมุมโค้ง
                border = BorderStroke(1.dp, Color.Gray), // เพิ่มเส้นขอบ
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Text(
                    text = selectedPetType?.Pet_name_type ?: "เลือกประเภท",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            petTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.Pet_name_type) },
                    onClick = {
                        onPetTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}



//@Composable
//fun PetCard2(pet: petMember, onDelete: () -> Unit, navController: NavHostController) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//    ) {
//
//        Column(modifier = Modifier.padding(25.dp)) {
//            Text("ชื่อสัตว์เลี้ยง: ${pet.petName}", fontSize = 16.sp)
//            Text("ประเภท: ${pet.petTypename}", fontSize = 16.sp)
//            Text(
//                "เพศ: ${if (pet.petGender == "M") "เพศผู้" else if (pet.petGender == "F") "เพศเมีย" else "ไม่ระบุ"}",
//                fontSize = 16.sp
//            )
//            Text("สายพันธุ์: ${pet.petBreed}", fontSize = 16.sp)
//            Text("อายุ: ${pet.petAge} ปี", fontSize = 16.sp)
//            Text("น้ำหนัก: ${pet.petWeight} กิโลกรัม", fontSize = 16.sp)
//            Text("คำแนะนำ / คำอธิบายเพิ่มเติม: ${pet.additionalInfo}", fontSize = 16.sp)
//
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 8.dp),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                Button(
//                    onClick = { navController.navigate("mypetedit/${pet.petID}") },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966))
//                ) {
//                    Text("แก้ไข", color = Color.Black)
//                }
//
//                Button(
//                    onClick = onDelete,
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xD7EE2C2C))
//                ) {
//                    Text("ลบ", color = Color.White)
//                }
//            }
//        }
//    }
//}