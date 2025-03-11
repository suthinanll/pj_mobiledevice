package com.example.ass07.customer.Mypet


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.ass07.admin.RadioGroupUsage
import com.example.ass07.customer.API.PetApi
import com.example.ass07.customer.Screen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun Mypetedit(navController: NavHostController, pet: petMember) {
    var textFieldPetName by remember { mutableStateOf(pet.petName) }
    var textFieldPetAge by remember { mutableStateOf(pet.petAge.toString()) }
    var textFieldPetWeight by remember { mutableStateOf(pet.petWeight.toString()) }
    var textFieldAdditionalInfo by remember { mutableStateOf(pet.additionalInfo) }
    var textFieldPetBreed by remember { mutableStateOf(pet.petBreed) }
    var petGender by rememberSaveable { mutableStateOf(if (pet.petGender == "M") "เพศผู้" else "เพศเมีย") }
    var isAddingPetType by remember { mutableStateOf(false) }

    val contextForToast = LocalContext.current
    val createClient = PetApi.create()
    val userId = pet.userId


    var petTypes by remember { mutableStateOf(listOf<PetType>()) }
    var selectedPetType by remember { mutableStateOf<PetType?>(null) }

    LaunchedEffect(Unit) {
        createClient.getPetTypes().enqueue(object : Callback<List<PetType>> {
            override fun onResponse(call: Call<List<PetType>>, response: Response<List<PetType>>) {
                if (response.isSuccessful) {
                    petTypes = response.body() ?: emptyList()
                    if (petTypes.isNotEmpty()) {
                        selectedPetType = petTypes.find { it.Pet_type_id == pet.Pet_type_id }
                    }
                }
            }

            override fun onFailure(call: Call<List<PetType>>, t: Throwable) {
                Log.e("PetTypes", "Failed to load: ${t.message}")
            }
        })
    }

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
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.navigate(Screen.MyPet.route) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "ย้อนกลับ", tint = Color.Black)
                    }

                    Text(
                        text = "แก้ไขข้อมูลสัตว์เลี้ยง",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                    )
                }

                OutlinedTextField(
                    value = textFieldPetName,
                    onValueChange = { textFieldPetName = it },
                    label = { Text("ชื่อสัตว์เลี้ยง") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                PetTypeDropdown(
                    petType = petTypes,
                    selectPetType = selectedPetType,
                    onPetTypeSelected = { selectedPetType = it }
                )

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
                    onValueChange = { newValue ->

                        if (newValue.isEmpty() || newValue.toIntOrNull()?.let { it >= 0 } == true) {
                            textFieldPetAge = newValue
                        }
                    },
                    label = { Text("อายุ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = textFieldPetWeight,
                    onValueChange = { newValue ->

                        if (newValue.isEmpty() || newValue.toDoubleOrNull()?.let { it >= 0 } == true) {
                            textFieldPetWeight = newValue
                        }
                    },
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
                    else -> pet.petGender
                }

                Button(
                    onClick = {
                        val petData = UpdatePetRequest(
                            pet_name = textFieldPetName,
                            pet_gender = genderCode,
                            pet_breed = textFieldPetBreed,
                            pet_age = textFieldPetAge.toIntOrNull() ?: 0,
                            pet_weight = textFieldPetWeight.toDoubleOrNull() ?: 0.0,
                            additional_info = textFieldAdditionalInfo,
                            pet_type_id = selectedPetType?.Pet_type_id ?: pet.Pet_type_id
                        )

                        Log.d("API_REQUEST", "Sending updatePet: $petData")

                        createClient.updatePet(pet.petID.toInt(), petData)
                            .enqueue(object : Callback<petMember> {
                                override fun onResponse(call: Call<petMember>, response: Response<petMember>) {
                                    if (response.isSuccessful) {
                                        Log.d("API_RESPONSE", "Update Successful: ${response.body()}")
                                        Toast.makeText(contextForToast, "อัปเดตสำเร็จ", Toast.LENGTH_SHORT).show()
                                        navController.navigate(Screen.MyPet.route)
                                    } else {
                                        Log.e("API_ERROR", "Update Failed: ${response.message()} - ${response.errorBody()?.string()}")
                                        Toast.makeText(contextForToast, "อัปเดตไม่สำเร็จ: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<petMember>, t: Throwable) {
                                    Log.e("API_ERROR", "Error updating pet: ${t.message}")
                                    Toast.makeText(contextForToast, "เกิดข้อผิดพลาด: ${t.message}", Toast.LENGTH_LONG).show()
                                }
                            })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD966)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("แก้ไข", color = Color.Black)
                }
            }
        }
    }
}