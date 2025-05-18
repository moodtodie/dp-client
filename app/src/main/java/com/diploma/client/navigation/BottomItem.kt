package com.diploma.client.navigation

import com.diploma.client.R

sealed class BottomItem(val title: String, val iconId: Int, val route: String) {
    object LogScreen : BottomItem("", R.drawable.icon, "log_screen")
    object ItemsScreen : BottomItem("", R.drawable.icon, "items_screen")
    object Scanner : BottomItem("", R.drawable.icon, "scanner_screen")
    object Notifications : BottomItem("", R.drawable.icon, "notifications_screen")
    object Screen5 : BottomItem("", R.drawable.icon, "screen_5")
}

//  Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "User Icon")