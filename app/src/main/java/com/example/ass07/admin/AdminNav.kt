package com.example.ass07.admin

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ass07.customer.NavGraph

class AdminNav {
    companion object {
        data class NavigationDrawerItemData(val label: String, val icon: ImageVector)
        @Composable
        fun MyScaffoldLayout() {
            val contextForToast = LocalContext.current.applicationContext
            val navController = rememberNavController()

            Scaffold(
                topBar = { MyTopAppBarAdmin(navController, contextForToast) },
                bottomBar = { MyBottomBarAdmin(navController, contextForToast) },
                floatingActionButtonPosition = FabPosition.End
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    NavGraph(navController = navController)
                }
            }
        }
        @OptIn(ExperimentalMaterial3Api::class)
        @Composable
        fun MyTopAppBarAdmin(navController: NavHostController, contextForToast: Context) {
            var expanded by remember { mutableStateOf(false) }

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Chill Pet Stay Admin",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFFFBC2B)
                ),
                actions = {


                }
            )
        }
        @Composable
        fun MyBottomBarAdmin(navController: NavHostController, contextForToast: Context) {
            val navigationItems = listOf(

                ScreenAdmin.ManageRoom,
                ScreenAdmin.Booking,
                ScreenAdmin.PetsAdmin,
                ScreenAdmin.Dashboard
            )

            var selectedScreen by remember { mutableStateOf(3) }

            NavigationBar(
                containerColor = Color(0xFFFFBC2B) // สีพื้นหลังของ Bottom Bar
            ) {
                navigationItems.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = screen.icon(),
                                contentDescription = null,
                                tint = if (selectedScreen == index) Color.White else Color.Black // ไอคอนสีขาวเมื่อถูกเลือก
                            )
                        },
                        label = {
                            Text(
                                text = screen.name,
                                color = if (selectedScreen == index) Color.White else Color.Black // ข้อความสีขาวเมื่อถูกเลือก
                            )
                        },
                        selected = selectedScreen == index,
                        onClick = {
                            selectedScreen = index
                            navController.navigate(screen.route)

                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Black,
                            unselectedTextColor = Color.Black,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}