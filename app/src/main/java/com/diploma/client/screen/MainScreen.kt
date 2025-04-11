package com.diploma.client.screen

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.diploma.client.navigation.BottomNav
import com.diploma.client.navigation.NavGraph
import com.journeyapps.barcodescanner.ScanOptions

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(context: Context, scanLauncher: ActivityResultLauncher<ScanOptions>) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { CenterAlignedTopAppBar(
            title = { Text(text = "Screen Title") },
            actions = {
                IconButton(onClick = {
                    navController.navigate("account_screen")
//                    Toast.makeText( context, "Need authorization", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "User Icon")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Gray
            )
        ) },
        content = { NavGraph(navHostController = navController, scanLauncher = scanLauncher) },
        bottomBar = { BottomNav(navController = navController) }
    )
}