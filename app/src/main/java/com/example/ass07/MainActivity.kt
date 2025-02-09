package com.example.ass07

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ass07.ui.theme.ASS07Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ASS07Theme {
                val navController = rememberNavController()
                MyScaffoldLayout(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScaffoldLayout(navController: androidx.navigation.NavHostController) {
    Scaffold(
        topBar = { MyTopAppBar(navController) }
    ) { innerPadding ->
        PaymentScreen(Modifier.padding(innerPadding))
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Chill Pet Stay") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFFFBC2B)
        )
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    ASS07Theme {
        val navController = rememberNavController()
        MyScaffoldLayout(navController)
    }
}
