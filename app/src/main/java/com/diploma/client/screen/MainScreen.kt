package com.diploma.client.screen

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.diploma.client.data.MainDb
import com.diploma.client.navigation.BottomNav
import com.diploma.client.navigation.NavGraph
import com.journeyapps.barcodescanner.ScanOptions
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    context: Context,
    scanLauncher: ActivityResultLauncher<ScanOptions>,
    db: MainDb,
    navControllerCallback: (NavHostController) -> Unit
) {
    val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "config",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    val navController = rememberNavController()
    LaunchedEffect(Unit) {
        navControllerCallback(navController)
    }

    var screenTitle by remember { mutableStateOf("Initial Title") }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(topBar = {
        if (currentRoute != "auth_screen") {
            CenterAlignedTopAppBar(title = { Text(text = screenTitle) }, actions = {
                if (currentRoute == "item/{itemId}") {
                    val itemId = backStackEntry?.arguments?.getString("itemId")
                    DeleteButton(UUID.fromString(itemId))
                } else {
                    IconButton(onClick = {
                        navController.navigate("account_screen")
//                    Toast.makeText( context, "Need authorization", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "User Icon"
                        )
                    }
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White, titleContentColor = Color.Gray
            )
            )
        }
    }, bottomBar = {
        if (currentRoute != "auth_screen" && currentRoute != "account_screen" && currentRoute != "item/{itemId}") BottomNav(
            navController = navController
        )
    }) { paddingValues ->
        NavGraph(
            context = context,
            preferences = sharedPreferences,
            onChangeTitle = { newTitle -> screenTitle = newTitle },
            navHostController = navController,
            scanLauncher = scanLauncher,
            modifier = Modifier.padding(paddingValues)
        )
    }
}