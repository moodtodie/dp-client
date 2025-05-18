package com.diploma.client

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.diploma.client.data.Item
import com.diploma.client.data.MainDb
import com.diploma.client.screen.MainScreen
import com.diploma.client.ui.theme.ClientTheme
import com.journeyapps.barcodescanner.ScanContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mainDb: MainDb
    private lateinit var navController: NavHostController

    private var counter = 0

    val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Unable to scan data", Toast.LENGTH_LONG).show()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                val item = mainDb.daoItem.findByBarcode(result.contents)

                if (item == null) {   //  Add new Item
                    mainDb.daoItem.insert(
                        Item(
                            id = UUID.randomUUID(),
                            name = "unknown item #${counter++}",
                            barcode = result.contents,
                            quantity = 1,
                            price = 1.5,
                        )
                    )
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Item \"${result.contents}\" has been added",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    //  Go to Item
                    withContext(Dispatchers.Main) {
                        navController.navigate("item/${item.id}")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        setContent {
            ClientTheme {
                MainScreen(
                    context = applicationContext,
                    scanLauncher = scanLauncher,
                    db = mainDb,
                ) { controller ->
                    navController = controller
                }
            }
        }
    }
}
