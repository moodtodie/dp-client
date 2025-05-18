package com.diploma.client.screen

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun ScannerScreen1(modifier: Modifier, scanLauncher: ActivityResultLauncher<ScanOptions>) {
    var scannedText: String = ""

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            scan(scanLauncher)
//            EmbeddedScanner(onScanResult = {result ->
//                scannedText = result
//            })
        }) {
            if (scannedText.isBlank())
                Text(text = "Tap here to scan QR code or barcode")
            else
                Text(text = scannedText)
        }
    }
}

@Composable
fun ScannerScreen(modifier: Modifier, scanLauncher: ActivityResultLauncher<ScanOptions>) {
    val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888).apply {
        eraseColor(android.graphics.Color.LTGRAY)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Размытое изображение
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Camera image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp) // 👈 Эффект blur от Accompanist
        )

        // Кнопка по центру
        Button(
            onClick = { scan(scanLauncher) },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Tap here to scan QR code or barcode")
        }
    }
}

private fun scan(scanLauncher: ActivityResultLauncher<ScanOptions>) {
    val options = ScanOptions()
    options.setPrompt("Scan a QR code or barcode")
    options.setCameraId(0)
    options.setBeepEnabled(false)
    options.setBarcodeImageEnabled(true)
    options.setOrientationLocked(false)
    scanLauncher.launch(options)
}

@Composable
fun EmbeddedScanner(
    onScanResult: (String) -> Unit
) {
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier
            .width(250.dp)
            .height(250.dp),
        factory = {
            val scannerView = DecoratedBarcodeView(context).apply {
                barcodeView.decoderFactory =
                    DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_128))
                initializeFromIntent(Intent()) // initialize defaults
                decodeContinuous(object : BarcodeCallback {
                    override fun barcodeResult(result: BarcodeResult?) {
                        result?.text?.let {
                            onScanResult(it)
                        }
                    }

                    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
                })
            }
            scannerView.resume()
            scannerView
        },
        update = { it.resume() }
    )
}