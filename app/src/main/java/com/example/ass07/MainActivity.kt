package com.example.ass07


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.ass07.admin.AdminNav
import com.example.ass07.ui.theme.ASS07Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ASS07Theme {
                //BB.MyScaffoldLayout()
                AdminNav.MyScaffoldLayout()
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






