package com.diploma.client.screen

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.diploma.client.service.authenticateRequest

@Composable
fun AuthScreen(
    context: Context,
    preferences: SharedPreferences,
    onSuccessAuth: (login: String) -> Unit
) {
    val savedLogin = preferences.getString("username", null)
    var login by remember { mutableStateOf(savedLogin ?: "") }
    var password by remember { mutableStateOf("") }
    var saveLogin by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            value = login,
            onValueChange = { login = it },
            label = { Text("Login") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(
                checked = saveLogin,
                onCheckedChange = { saveLogin = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save login")
        }

        Spacer(modifier = Modifier.height(16.dp * 2))

        Button(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            onClick = {

                if (saveLogin) preferences.edit().putString("username", login).apply()
                authenticateRequest(
                    context = context,
                    username = login, password = password,
                    onSuccess = {
                        onSuccessAuth(login)
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    })
            }) {
            Text(text = "Log in")
        }
    }
}