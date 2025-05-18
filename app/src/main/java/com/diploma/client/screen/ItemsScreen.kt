package com.diploma.client.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.diploma.client.R
import com.diploma.client.data.Item
import com.diploma.client.data.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import android.graphics.Color as color

val itemFontSize = 20.sp
val itemListFontSize = 20.sp

@Composable
fun ItemsScreen(
    modifier: Modifier,
    itemsStateList: State<List<Item>>,
    navController: NavController
) { //  Items
    if (itemsStateList.value.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.empty_list))
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
            ) {
                items(itemsStateList.value) { product ->
                    ItemList(item = product, onClick = {
                        navController.navigate("item/${product.id}")
                    })
                }
            }
            Button(
                onClick = {
                    /* TODO: Add new item logic */
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Text(stringResource(R.string.add_item))
            }
        }
    }
}

@Composable
fun ItemList(item: Item, onClick: () -> Unit) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = item.name,
                fontSize = itemListFontSize,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
            Text(
                text = item.quantity.toString(),
                fontSize = itemListFontSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.15f)
            )
            Text(
                text = item.price.toString(),
                fontSize = itemListFontSize,
                textAlign = TextAlign.Left,
                modifier = Modifier.fillMaxWidth(0.25f)
            )
        }
    }
}

@Composable
fun ItemScreen(modifier: Modifier, context: Context, item: Item) {
    val viewModel: MainViewModel = hiltViewModel()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.TopStart)
        ) {
            EditableNameRow(
                name = item.name,
                onNameChange = { newName ->
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.updateItem(item = item.copy(name = newName))
                    }
                }
            )
            EditableQuantityRow(
                quantity = item.quantity,
                onQuantityChange = { newQuantity ->
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.updateItem(item = item.copy(quantity = newQuantity))
                    }
                }
            )
            EditablePriceRow(
                price = item.price,
                onPriceChange = { newPrice ->
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.updateItem(item = item.copy(price = newPrice))
                    }
                }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                Text(
                    text = "Shop ID:",
                    fontSize = itemFontSize,
                    modifier = Modifier.fillMaxWidth(0.3f)
                )
                Text(
                    text = item.shopId.toString(),
                    fontSize = itemFontSize,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
        }

        val barcodeBitmap = remember(item.barcode) { generateBarcode(item.barcode) }
        
        Column(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomStart)
                .clickable {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Copy", item.barcode)
                    clipboard.setPrimaryClip(clip)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            barcodeBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Barcode",
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.barcode,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

@Composable
fun DeleteButton(
    itemId: UUID
) {
    val viewModel: MainViewModel = hiltViewModel()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Button(
        onClick = { showDeleteDialog = true },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.Gray
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(40.dp)
    ) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_item)) },
            text = { Text("Вы уверены, что хотите удалить этот товар?") },
            confirmButton = {
                TextButton(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.deleteItem(item = viewModel.findItem(uuid = itemId)!!)
                    }
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun EditableNameRow(
    name: String,
    onNameChange: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(name) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { showDialog = true }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "Name:",
            fontSize = itemFontSize,
            modifier = Modifier.fillMaxWidth(0.3f)
        )
        Text(
            text = name,
            fontSize = itemFontSize,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Name") },
            text = {
                TextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("New name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onNameChange(editedName)
                        showDialog = false
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

@Composable
fun EditableQuantityRow(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var editedQuantity by remember { mutableStateOf(quantity.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { showDialog = true }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "Quantity:",
            fontSize = itemFontSize,
            modifier = Modifier.fillMaxWidth(0.3f)
        )
        Text(
            text = quantity.toString(),
            fontSize = itemFontSize,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Quantity") },
            text = {
                TextField(
                    value = editedQuantity,
                    onValueChange = { editedQuantity = it },
                    label = { Text("New quantity") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        editedQuantity.toIntOrNull()?.let {
                            onQuantityChange(it)
                        }
                        showDialog = false
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

@Composable
fun EditablePriceRow(
    price: Double,
    onPriceChange: (Double) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var editedPrice by remember { mutableStateOf(price.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { showDialog = true }
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "Price:",
            fontSize = itemFontSize,
            modifier = Modifier.fillMaxWidth(0.3f)
        )
        Text(
            text = price.toString(),
            fontSize = itemFontSize,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Price") },
            text = {
                TextField(
                    value = editedPrice,
                    onValueChange = { editedPrice = it },
                    label = { Text("New price") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        editedPrice.toDoubleOrNull()?.let {
                            onPriceChange(it)
                        }
                        showDialog = false
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

fun generateBarcode(data: String): Bitmap? {
    return try {
        val writer = com.google.zxing.MultiFormatWriter()
        val bitMatrix = writer.encode(
            data,
            com.google.zxing.BarcodeFormat.CODE_128, // Можно заменить на QR_CODE, EAN_13 и др.
            600,
            300
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (bitMatrix.get(x, y)) color.BLACK else color.WHITE
            }
        }

        Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    } catch (e: Exception) {
        null
    }
}
