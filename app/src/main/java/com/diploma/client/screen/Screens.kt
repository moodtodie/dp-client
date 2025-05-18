package com.diploma.client.screen

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.diploma.client.NotificationWorker
import com.diploma.client.R
import com.diploma.client.data.ActionLog
import com.diploma.client.data.MainViewModel
import com.diploma.client.data.User
import com.diploma.client.service.NsdFinder
import com.diploma.client.service.changePassword
import com.diploma.client.service.getLogs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@Composable
fun ActionLogList(logs: List<ActionLog>) {
    val sortedLogs = remember(logs) {
        logs.sortedByDescending { it.createdAt }
    }

    LazyColumn {
        items(sortedLogs) { log ->
            Text(
                text = buildString {
                    append("[${log.createdAt}] ")
                    append("${log.createdBy}\n")
                    append("${log.action} ")
                    append("${log.entity}\n")
                    append(log.details)
                }, modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            HorizontalDivider()
        }
    }
}

// DEBUG CODE START
fun parseToTimestamp(minskDateTimeString: String): Timestamp {
    // Парсим строку как LocalDateTime (без учёта часового пояса)
    val localDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.parse(minskDateTimeString)
    } else {
        TODO("VERSION.SDK_INT < O")
    }

    // Привязываем его к часовому поясу Минска
    val zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("Europe/Minsk"))

    // Преобразуем в Timestamp
    return Timestamp.from(zonedDateTime.toInstant()) as Timestamp
}
// DEBUG CODE END

@Composable
fun LogScreen(
    modifier: Modifier,
    context: Context,
) { // TODO: History Screen
    var logs = listOf<ActionLog>()

    getLogs(context = context, /* TODO: Add date*/ from = "", to = "",
        onSuccess = { list ->
            logs = list
        }, onError = { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        })

    // DEBUG CODE START
    val viewModel: MainViewModel = hiltViewModel()
    val usersList = viewModel.getUsers().collectAsState(initial = emptyList())
    val itemList = viewModel.getItems().collectAsState(initial = emptyList()).value

    val userIvan = usersList.value.find { it.username == "ivan.ivanov" }
    val userOleg = usersList.value.find { it.username == "oleg.olegovich" }

    if (itemList.isNotEmpty()) {
        logs = listOf(
            ActionLog(
                logUUID = UUID.randomUUID(),
                createdAt = Timestamp.valueOf("2025-05-12 13:00:43.705"),
                createdBy = userIvan!!.username,
                entity = "ITEM",
                action = "CREATE",
                details = "${itemList[0].id}(${itemList[0].name})"
            ),
            ActionLog(
                logUUID = UUID.randomUUID(),
                createdAt = Timestamp.valueOf("2025-05-12 12:56:12.525"),
                createdBy = userIvan.username,
                entity = "ITEM",
                action = "CREATE",
                details = "${itemList[1].id}(${itemList[1].name})"
            ),
            ActionLog(
                logUUID = UUID.randomUUID(),
                createdAt = Timestamp.valueOf("2025-05-12 14:31:56.207"),
                createdBy = userIvan.username,
                entity = "ITEM",
                action = "UPDATE",
                details = "${itemList[0].id}(${itemList[0].name})"
            ),
            ActionLog(
                logUUID = UUID.randomUUID(),
                createdAt = Timestamp.valueOf("2025-05-11 11:05:52.836"),
                createdBy = "Admin",
                entity = "USER",
                action = "CREATE",
                details = "${userIvan.id}(${userIvan.username})"
            ),
            ActionLog(
                logUUID = UUID.randomUUID(),
                createdAt = Timestamp.valueOf("2025-05-11 11:03:19.341"),
                createdBy = "Admin",
                entity = "USER",
                action = "CREATE",
                details = "${userOleg!!.id}(${userOleg.username})"
            )
        )
    }
    // DEBUG CODE END

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (logs.isEmpty()) {
            Text(
                modifier = Modifier.align(Alignment.Center), text = "No history"
            )
        } else {
            ActionLogList(logs)
        }
    }

//        LazyColumn(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            items(notificationStateList) { message ->
//                Text(
//                    text = message,
//                    style = MaterialTheme.typography.bodyLarge,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp)
//                        .clickable { notificationStateList.remove(message) }
//                )
//            }
//        }
}

@Composable
fun NotificationsScreen(
    modifier: Modifier,
    context: Context,
    notificationStateList: SnapshotStateList<String>,
    onClick: () -> Unit
) {
    Button(modifier = modifier.fillMaxWidth(), onClick = {
        val delayInMillis = TimeUnit.SECONDS.toMillis(15)

        val request = OneTimeWorkRequestBuilder<NotificationWorker>().setInitialDelay(
            delayInMillis, TimeUnit.MILLISECONDS
        ).build()

        WorkManager.getInstance(context).enqueue(request)


        notificationStateList.add("Сообщение #$")
//            onClick()
    }) {
        Text("Notify")
    }

    if (notificationStateList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text("No notifications")
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
//            Button(
//                onClick = {
//                    notificationStateList.add("Сообщение #$")
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(text = "Добавить сообщение")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(notificationStateList) { message ->
                    Text(text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { notificationStateList.remove(message) })
                }
            }
        }
    }
}

var addr = ""

@Composable
fun Screen5(modifier: Modifier, context: Context) { //  List of other options
    val nsdHelper = NsdFinder(context)
//    Box(modifier = modifier.fillMaxSize()) {
//        if (addr.isEmpty()) Button(modifier = Modifier.align(Alignment.Center), onClick = {
//            nsdHelper.discoverService { ip, port ->
//                addr = "$ip:$port"
////                Log.d("NSD", "Сервер найден: http://$ip:$port")
//                // можно делать API-запросы на этот адрес
//            }
//            if (addr.isEmpty()) Toast.makeText(context, "Failed. Try again...", Toast.LENGTH_SHORT)
//                .show()
//        }) {
//            Text(text = "Get IP")
//        }
//        else Text(modifier = Modifier.align(Alignment.Center), text = "IP: $addr")
//    }

    Box(modifier = modifier.fillMaxSize()) {
        val viewModel: MainViewModel = hiltViewModel()

        val shop1Id = UUID.randomUUID()

        Button(modifier = Modifier.align(Alignment.Center), onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.insertUser(
                    User(
                        id = UUID.randomUUID(),
                        username = "Admin",
                        role = "ADMIN",
                        shopId = null
                    )
                )
                viewModel.insertUser(
                    User(
                        id = UUID.randomUUID(),
                        username = "ivan.ivanov",
                        role = "ADMIN",
                        shopId = shop1Id
                    )
                )
                viewModel.insertUser(
                    User(
                        id = UUID.randomUUID(),
                        username = "oleg.olegovich",
                        role = "USER",
                        shopId = shop1Id
                    )
                )
            }
        }
        ) {
            Text(text = "press me")
        }
    }

//    Text(
//        text = "Screen 5",
//        modifier = Modifier
//            .fillMaxSize()
//            .wrapContentHeight(),
//        textAlign = TextAlign.Center,
//    )
}

@SuppressLint("ServiceCast")
@Composable
fun AccountScreen(modifier: Modifier, context: Context, user: User, onClick: () -> Unit) {
    val fontSize = 20.sp

    var showDialog by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
        ) {
            Text(
                text = "${stringResource(R.string.name)}: ${user.username}", fontSize = fontSize
            )
            Text(text = "${stringResource(R.string.role)}: ${user.role}", fontSize = fontSize)
            Text(
                text = "${stringResource(R.string.shop_id)}: ${user.shopId}",
                fontSize = (fontSize.div(1.2)),
                color = Color.Gray,
                modifier = Modifier.clickable {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Copy", user.shopId.toString())
                    clipboard.setPrimaryClip(clip)
                })
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    showDialog = true
                }) {
                Text(stringResource(R.string.change_passwd))
            }
        }
        Button(modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth(),
            onClick = { onClick() }) {
            Text(text = "Go back")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.change_passwd)) },
            text = {
                Column {
                    TextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Old password") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New password") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        changePassword(context = context,
                            oldPassword = oldPassword,
                            newPassword = newPassword,
                            onSuccess = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                showDialog = false
                            },
                            onError = { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            })
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}