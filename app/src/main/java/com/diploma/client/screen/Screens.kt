package com.diploma.client.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Screen1() {
    Text(text = "Screen 1",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(),
        textAlign = TextAlign.Center,)
}

@Composable
fun Screen2() { //  Items
    Text(text = "Screen 2",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(),
        textAlign = TextAlign.Center,)
}

//@Composable
//fun Screen3() {
//    Text(text = "Screen 3",
//        modifier = Modifier.fillMaxSize().wrapContentHeight(),
//        textAlign = TextAlign.Center,)
//}

@Composable
fun Screen4() { //  Notifications
    Text(text = "Screen 4",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(),
        textAlign = TextAlign.Center,)
}

@Composable
fun Screen5() { //  List of other options
    Text(text = "Screen 5",
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(),
        textAlign = TextAlign.Center,)
}

@Composable
fun AccountScreen(onClick: () -> Unit) { //  List of other options
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Account Screen",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = { onClick() }) {
            Text(text = "Go to Main menu")
        }
    }
}