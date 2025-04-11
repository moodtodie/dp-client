package com.diploma.client.screen

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun ScannerScreen(scanLauncher: ActivityResultLauncher<ScanOptions>) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            scan(scanLauncher)
        }) {
            Text(text = "Tap here to scan QR code or barcode")
        }
    }
}

private fun scan(scanLauncher: ActivityResultLauncher<ScanOptions>) {
    val options = ScanOptions()
    options.setPrompt("Scan a QR code or barcode")
    options.setCameraId(0)
    options.setBeepEnabled(false)
    options.setBarcodeImageEnabled(true)
//        options.captureActivity = "portrait"
    options.setOrientationLocked(false)
    scanLauncher.launch(options)
}