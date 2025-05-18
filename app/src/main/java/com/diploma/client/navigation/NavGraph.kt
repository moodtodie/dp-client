package com.diploma.client.navigation

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.diploma.client.R
import com.diploma.client.data.Item
import com.diploma.client.data.MainViewModel
import com.diploma.client.data.User
import com.diploma.client.screen.AccountScreen
import com.diploma.client.screen.AuthScreen
import com.diploma.client.screen.ItemScreen
import com.diploma.client.screen.ItemsScreen
import com.diploma.client.screen.LogScreen
import com.diploma.client.screen.NotificationsScreen
import com.diploma.client.screen.ScannerScreen
import com.diploma.client.screen.Screen5
import com.diploma.client.service.getItems
import com.diploma.client.service.getUser
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun NavGraph(
    context: Context,
    preferences: SharedPreferences,
    onChangeTitle: (String) -> Unit,
    navHostController: NavHostController,
    scanLauncher: ActivityResultLauncher<ScanOptions>,
    modifier: Modifier
) {
    val viewModel: MainViewModel = hiltViewModel()

    val itemsStateList = viewModel.getItems().collectAsState(initial = emptyList())
    val usersStateList = viewModel.getUsers().collectAsState(initial = emptyList())
    val notificationStateList = remember { mutableStateListOf<String>() }

    NavHost(navController = navHostController, startDestination = "auth_screen") {
        composable("auth_screen") {
            AuthScreen(context = context, preferences = preferences, onSuccessAuth = { login ->
                getUser(
                    context = context,
                    username = login,
                    onSuccess = { user ->
                        var items = listOf<Item>()
                        getItems(
                            context = context,
                            shopId = if (user.shopId != null) user.shopId.toString() else null,
                            onSuccess = { list -> items = list },
                            onError = { error ->
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            })
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.insertUser(user = user)
                        }
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    })
                navHostController.navigate("scanner_screen")
            })
        }
        composable("log_screen") {
            onChangeTitle(stringResource(R.string.log_screen))
            LogScreen(modifier = modifier, context = context)
        }
        composable("items_screen") {
            onChangeTitle(stringResource(R.string.items_screen))
            ItemsScreen(modifier = modifier, itemsStateList, navHostController)
        }
        composable(
            route = "item/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            val item = itemsStateList.value.find { it.id.toString() == itemId }

            item?.let {
                onChangeTitle(it.name)
                ItemScreen(modifier = modifier, context =context , it)
            }
        }
        composable("scanner_screen") {
            onChangeTitle(stringResource(R.string.scanner_screen))
            ScannerScreen(modifier = modifier, scanLauncher = scanLauncher)
        }
        composable("notifications_screen") {
            onChangeTitle(stringResource(R.string.notifications_screen))
            NotificationsScreen(
                context = context,
                modifier = modifier,
                notificationStateList = notificationStateList,
                onClick = {
//                CoroutineScope(Dispatchers.IO).launch {
//                    viewModel.insertItem(Item(UUID.randomUUID(), "xueta", "123", 1, 1.5))
//                }
                })
        }
        composable("screen_5") {
            onChangeTitle(stringResource(R.string.screen_5_screen))
            Screen5(modifier = modifier, context = context)
        }
        composable("account_screen") {
            onChangeTitle(stringResource(R.string.account_screen))
            val username = preferences.getString("username", null)
            var user = usersStateList.value.find { it.username == username }

            if (user == null) user =
                User(UUID.randomUUID(), username = username!!, role = "unknown")

            AccountScreen(modifier = modifier, context = context, user = user) {
                navHostController.navigate("scanner_screen")
            }
        }
    }
}