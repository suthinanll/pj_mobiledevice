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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.ass07.admin.AdminNav
import com.example.ass07.customer.NavGraph
import com.example.ass07.ui.theme.ASS07Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ASS07Theme {

//                BB.MyScaffoldLayout()
                AdminNav.MyScaffoldLayout()
//                MyScreen()
            }

            }
        }
    }


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ASS07Theme {
        //BB.MyScaffoldLayout()
        AdminNav.MyScaffoldLayout()
    }
}
@Composable
fun MyScreen(){
    val navController = rememberNavController()
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){  }
    NavGraph(navController)
}





