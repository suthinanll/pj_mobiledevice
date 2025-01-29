package com.example.ass07

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyPet() {
    MyScreen()
}
    @Composable
    fun MyRadioGroup(
        mItems:List<String>,
        selected:String,
        setSelected:(selected:String)->Unit,
    ){
        Row{
            mItems.forEach{item->
                Row(verticalAlignment=Alignment.CenterVertically){
                    RadioButton(
                        selected=selected==item,
                        onClick={setSelected(item)},
                        enabled=true,
                        colors= RadioButtonDefaults.colors(
                            selectedColor= Color.Magenta
                        )
                    )
                    Text(
                        text=item,
                        modifier=Modifier.padding(start=1.dp)
                    )
                }
            }
        }
    }
    @Composable
    fun RadioGroupUsage(
        selected:String,
        setSelected:(String)->Unit,
        modifier:Modifier=Modifier
    ){

        val genderList = listOf("Male", "Female", "Other")
        Column{
            Text(
                text="Gender:$selected",
            )
            MyRadioGroup(
                mItems=genderList,
                selected=selected,
                setSelected=setSelected
            )
        }
    }


    @Composable
    fun MyScreen() {


        var albumItemsList = remember { mutableStateListOf<Member>() }
        val contextForToast = LocalContext.current.applicationContext
        var addDialog by remember { mutableStateOf(false) }
        var deleteDialog by remember { mutableStateOf(false) }
        var itemClick by remember { mutableStateOf(Member("", "", "",0)) }
        var textFieldName by remember { mutableStateOf("") }
        var textFieldEmail by remember { mutableStateOf("") }
        var textFieldSalary by remember { mutableStateOf("") }
        var gender by rememberSaveable{ mutableStateOf("") }




        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(0.85f)) {
                    Text(text = "Member Lists:", fontSize = 25.sp)
                }
                Button(onClick = { addDialog = true }) {
                    Text(text = "Add Album", fontSize = 17.sp)
                }
            }


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize().background(Color.White) // กำหนดสีพื้นหลังของ LazyColumn
            ) {
                itemsIndexed(items = albumItemsList) { index, item ->
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White), // การ์ดเป็นพื้นหลังสีขาว
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(16.dp),
                        onClick = {
                            Toast.makeText(
                                contextForToast,
                                "Click on ${item.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = """
                        Name: ${item.name}
                        Gender: ${item.gender}
                        E-mail: ${item.email}
                        Salary: ${item.salary}
                    """.trimIndent(),
                                fontSize = 16.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color.Yellow, shape = RoundedCornerShape(8.dp)) // พื้นหลังสีเหลือง
                                        .padding(horizontal = 12.dp, vertical = 6.dp) // ระยะห่าง
                                        .clickable {
                                            itemClick = item
                                            deleteDialog = true
                                            Toast.makeText(
                                                contextForToast,
                                                "Click on Delete ${itemClick.name}.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                ) {
                                    Text(
                                        text = "Delete",
                                        color = Color.White, // ตัวอักษรสีขาว
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }



            if (addDialog) {
                AlertDialog(
                    onDismissRequest = { addDialog = false },
                    title = { Text(text = "Enter Information") },
                    text = {
                        Column {


                            OutlinedTextField(
                                value = textFieldName,
                                onValueChange = { textFieldName = it },
                                label = { Text(text = "Enter your Name") }
                            )
                            RadioGroupUsage(
                                selected=gender,
                                setSelected={gender=it}
                            )
                            OutlinedTextField(
                                value = textFieldEmail,
                                onValueChange = { textFieldEmail = it },
                                label = { Text(text = "Enter your email") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email
                                )
                            )
                            OutlinedTextField(
                                value = textFieldSalary,
                                onValueChange = { textFieldSalary = it },
                                label = { Text(text = "Enter your Salary") },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )

                        }

                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                addDialog = false
                                albumItemsList.add(
                                    Member(


                                        textFieldName,
                                        gender = gender,
                                        textFieldEmail,
                                        textFieldSalary.toInt(),
                                    )
                                )
                                textFieldName = ""
                                textFieldEmail = ""
                                textFieldSalary = "0"
                                gender = ""
                            }
                        ) {
                            Text(text = "Register")
                        }
                    },
                    dismissButton = {
                        TextButton(


                            onClick = {
                                addDialog = false
                                textFieldName = ""
                                textFieldEmail = ""
                                textFieldSalary = "0"
                                gender = ""
                            }
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                )
            }
            if (deleteDialog) {
                AlertDialog(
                    onDismissRequest = { deleteDialog = false },
                    title = { Text(text = "Warning") },
                    text = { Text(text = "Are you sure you want to delete ${itemClick.name}?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                deleteDialog = false
                                Toast.makeText(
                                    contextForToast,
                                    "Yes, ${itemClick.name} is deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                                albumItemsList.remove(itemClick)
                            }
                        ) {
                            Text(text = "Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                deleteDialog = false
                                Toast.makeText(
                                    contextForToast,
                                    "Click on No",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Text(text = "No")
                        }
                    }
                )
            }
        }
    }
@Composable
fun DialogButton(
    modifier: Modifier = Modifier,
    cornerRadiusPercent: Int = 26,
    buttonText: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = cornerRadiusPercent))
            .background(color = Color.Yellow)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true)
            ) { onDismiss() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buttonText,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
