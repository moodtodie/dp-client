package com.diploma.client.navigation

import com.diploma.client.R

sealed class BottomItem (val title: String, val iconId: Int, val route: String) {
    object Screen1: BottomItem("Screen 1", R.drawable.icon, "screen_1")
    object Screen2: BottomItem("Screen 2", R.drawable.icon, "screen_2")
    object Screen3: BottomItem("Scanner", R.drawable.icon, "scanner_screen")
    object Screen4: BottomItem("Screen 4", R.drawable.icon, "screen_4")
    object Screen5: BottomItem("Screen 5", R.drawable.icon, "screen_5")
}