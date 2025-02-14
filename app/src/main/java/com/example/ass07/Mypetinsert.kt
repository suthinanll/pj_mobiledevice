package com.example.ass07

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Mypetinsert(navController: NavHostController) {
    var textFieldPetName by remember { mutableStateOf("") }
    var textFieldPetAge by remember { mutableStateOf("") }
    var textFieldPetWeight by remember { mutableStateOf("") }
    var textFieldAdditionalInfo by remember { mutableStateOf("") }
    var textFieldPetBreed by remember { mutableStateOf("") }
    var petGender by rememberSaveable { mutableStateOf("") }
    var petTypename by rememberSaveable { mutableStateOf("") }
    var UserId by remember { mutableStateOf("") }


    val createClient = PetApi.create()
    val contextForToast = LocalContext.current
    val userId = 3 // หรือส่งผ่าน parameter หรือดึงจาก session





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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigate(Screen.MyPet.route) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "ย้อนกลับ",
                            tint = Color.Black
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        Text(
                            text = "เพิ่มข้อมูลสัตว์เลี้ยง",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center ,
                            modifier = Modifier
                                .fillMaxWidth()


                        )
                    }
                }

                OutlinedTextField(
                    value = textFieldPetName,
                    onValueChange = { textFieldPetName = it },
                    label = { Text("ชื่อสัตว์เลี้ยง") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))


                var petTypes by remember { mutableStateOf(listOf<PetType>()) }
                var selectedPetType by remember { mutableStateOf<PetType?>(null) }

// เรียกข้อมูลจาก API
                val createClient = PetApi.create()
                LaunchedEffect(Unit) {
                    createClient.getPetTypes()
                        .enqueue(object : Callback<List<PetType>> {
                            override fun onResponse(
                                call: Call<List<PetType>>,
                                response: Response<List<PetType>>
                            ) {
                                if (response.isSuccessful) {
                                    petTypes = response.body() ?: emptyList()
                                    Log.d("PetTypes", "Loaded: $petTypes") // ✅ Debug เช็คค่าที่โหลดมา
                                    if (petTypes.isNotEmpty()) {
                                        selectedPetType = petTypes[0]
                                        petTypename = petTypes[0].Pet_name_type
                                    }
                                }
                            }
                            override fun onFailure(call: Call<List<PetType>>, t: Throwable) {
                                Log.e("PetTypes", "Failed to load: ${t.message}") // ✅ Debug เช็ค error
                            }
                        })
                }

// ค่า Pet_type_id จะได้จากประเภทที่เลือก
                val Pet_type_id = selectedPetType?.Pet_type_id?.toString() ?: ""

                if (petTypes.isNotEmpty()) {
                    RadioGroupUsage(
                        selected = petTypename,
                        setSelected = { newTypeName ->
                            petTypename = newTypeName
                            selectedPetType = petTypes.find { it.Pet_name_type == newTypeName }
                        },
                        label = "ประเภท",
                        options = petTypes.map { it.Pet_name_type } // ✅ แสดง Pet_name_type จากฐานข้อมูล
                    )
                } else {
                    Text("กำลังโหลดประเภทสัตว์...", fontSize = 16.sp, color = Color.Gray)
                }

                RadioGroupUsage(
                    selected = petGender,
                    setSelected = { petGender = it },
                    label = "เพศ",
                    options = listOf("เพศผู้", "เพศเมีย")
                )

                OutlinedTextField(
                    value = textFieldPetBreed,
                    onValueChange = { textFieldPetBreed = it },
                    label = { Text("สายพันธุ์") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = textFieldPetAge,
                    onValueChange = { textFieldPetAge = it },
                    label = { Text("อายุ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = textFieldPetWeight,
                    onValueChange = { textFieldPetWeight = it },
                    label = { Text("น้ำหนัก") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = textFieldAdditionalInfo,
                    onValueChange = { textFieldAdditionalInfo = it },
                    label = { Text("คำแนะนำ / คำอธิบายเพิ่มเติม") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                val genderCode = when (petGender) {
                    "เพศผู้" -> "M"
                    "เพศเมีย" -> "F"
                    else -> ""
                }

                Button(
                    onClick = {
                        createClient.insertPet(
                            textFieldPetName,
                            genderCode,
                            textFieldPetBreed,
                            textFieldPetAge.toIntOrNull() ?: 0,
                            textFieldPetWeight.toIntOrNull() ?: 0,
                            textFieldAdditionalInfo,
                            Pet_type_id.toInt(),  // รหัสประเภท
                            userId       // รหัสผู้ใช้
                        ).enqueue(object : Callback<petMember> {
                            override fun onResponse(call: Call<petMember>, response: Response<petMember>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(contextForToast, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Screen.MyPet.route)
                                } else {
                                    Toast.makeText(contextForToast, "บันทึกไม่สำเร็จ: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onFailure(call: Call<petMember>, t: Throwable) {
                                Toast.makeText(contextForToast, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_LONG).show()
                            }
                        })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("เพิ่มข้อมูล", color = Color.Black)
                }
            }
        }
    }
}


@Composable
fun RadioGroupUsage(
    selected: String,
    setSelected: (String) -> Unit,
    label: String,
    options: List<String>
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "$label: $selected",
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            options.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selected == item,
                        onClick = { setSelected(item) },
                        enabled = true,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Magenta
                        )
                    )
                    Text(text = item)
                }
            }
        }
    }
}
