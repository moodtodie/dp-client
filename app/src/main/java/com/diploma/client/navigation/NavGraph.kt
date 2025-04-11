package com.diploma.client.navigation

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.diploma.client.screen.AccountScreen
import com.diploma.client.screen.ScannerScreen
import com.diploma.client.screen.Screen1
import com.diploma.client.screen.Screen2
import com.diploma.client.screen.Screen4
import com.diploma.client.screen.Screen5
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun NavGraph (
    navHostController: NavHostController,
    scanLauncher: ActivityResultLauncher<ScanOptions>
){
    NavHost(navController = navHostController, startDestination = "scanner_screen"){
        composable("screen_1"){
            Screen1()
        }
        composable("screen_2"){
            Screen2()
        }
        composable("scanner_screen"){
            ScannerScreen(scanLauncher)
        }
        composable("screen_4"){
            Screen4()
        }
        composable("screen_5"){
            Screen5()
        }
        composable("account_screen"){
            AccountScreen {
                navHostController.navigate("scanner_screen")
            }
        }
    }
}