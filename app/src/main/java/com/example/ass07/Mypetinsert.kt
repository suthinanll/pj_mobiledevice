package com.example.ass07

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
    var Pet_type_id by rememberSaveable { mutableStateOf("") }


    val createClient = PetApi.create()
    val contextForToast = LocalContext.current

    if (petTypename == "สุนัข"){
        Pet_type_id.equals(1)
    }else{
        Pet_type_id.equals(2)
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
                Text(text = "เพิ่มข้อมูลสัตว์เลี้ยง", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

                OutlinedTextField(
                    value = textFieldPetName,
                    onValueChange = { textFieldPetName = it },
                    label = { Text("ชื่อสัตว์เลี้ยง") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

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

                Button(
                    onClick = {
                        createClient.insertPet(
                            textFieldPetName,
                            petGender,
                            textFieldPetBreed,
                            textFieldPetAge.toIntOrNull() ?: 0,
                            textFieldPetWeight.toIntOrNull() ?: 0,
                            textFieldAdditionalInfo,
                            Pet_type_id,
                            petTypename,
                            UserId.toInt()
                        ).enqueue(object : Callback<petMember> {
                            override fun onResponse(call: Call<petMember>, response: Response<petMember>) {
                                Toast.makeText(contextForToast, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.MyPet.route)
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
