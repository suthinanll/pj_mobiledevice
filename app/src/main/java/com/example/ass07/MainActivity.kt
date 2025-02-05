package com.example.ass07

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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


import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.ass07.ui.theme.ASS07Theme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ASS07Theme {
BB.MyScaffoldLayout()            }
            }
        }
    }


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ASS07Theme {
        BB.MyScaffoldLayout()
    }
}

@Composable
fun Login() {
    val contextForToast = LocalContext.current
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3D9))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(vertical = 20.dp)
                .clip(RoundedCornerShape(20.dp)),
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
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    Toast.makeText(contextForToast, "Logged in as ${username.value}", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFBC2B),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalArrangement = Arrangement.End
            ) {

            }
        }
    }
}


@Composable
fun Register() {
    var name by remember { mutableStateOf("") }
    var phonenumber by remember { mutableStateOf("") }
    var em by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }


    val createClient = projectApi.create()

    val contextForToast = LocalContext.current.applicationContext



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3D9))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(vertical = 20.dp)
                .clip(RoundedCornerShape(20.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {


            Text(
                text = "Register",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                singleLine = true
            )
            OutlinedTextField(
                value = phonenumber,
                onValueChange = { phonenumber = it },
                label = { Text("Phone number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = em,
                onValueChange = { em = it },
                label = { Text("emsil") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(10.dp))


            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = pw,
                onValueChange = { pw = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = {
                    createClient.insertuser(
                        name,phonenumber.toInt(),em,pw
                    ).enqueue(object : Callback<users> {
                        override fun onResponse(call: Call<users>, response: Response<users>) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    contextForToast,
                                    "Successfully Inserted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    contextForToast,
                                    "Insertion Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<users>, t: Throwable) {
                            Toast.makeText(
                                contextForToast,
                                "Error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFBC2B),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Register")
            }

        }
    }


}


