package com.diploma.client

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.diploma.client.screen.MainScreen
import com.diploma.client.data.MainDb

import com.diploma.client.ui.theme.ClientTheme
import com.journeyapps.barcodescanner.ScanContract
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var mainDb: MainDb

     val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null){

        } else {
            Toast.makeText( this, "Scan data: \"${result.contents}\"", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientTheme {
                MainScreen(applicationContext, scanLauncher)
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "World",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AccountListItem(name: String = "unknown", role: String = "чушпан") {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(10.dp),
////        elevation = 5.dp,
//        shape = RoundedCornerShape(15.dp)
//    ) {
////        Image(imageVector = , contentDescription = )
//        Column {
//            Text(text = name)
//            Text(text = role)
//        }
//    }
//}
