package com.example.ass07


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.ass07.admin.AdminNav
import com.example.ass07.customer.BB
import com.example.ass07.customer.HotelBookingScreen
import com.example.ass07.customer.NavGraph
import com.example.ass07.ui.theme.ASS07Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val roomId = 1


            ASS07Theme {
                BB.MyScaffoldLayout()
//                AdminNav.MyScaffoldLayout()
//                MyScreen()
//                HotelBookingScreen(navController)
//                NavGraph(navController) // ถ้าไม่มีมันจะไม่ไปหน้าจองทำไมกันใครทราบที่แน่ๆผมไม่ทราบ


//                BB.MyScaffoldLayout()
//                AdminNav.MyScaffoldLayout()
                //MyScreen()

            }

            }

        }
    }





@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ASS07Theme {
        BB.MyScaffoldLayout()
        AdminNav.MyScaffoldLayout()
    }
}
@Composable
fun MyScreen(){
    val navController = rememberNavController()
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

    }
    NavGraph(navController)
}




