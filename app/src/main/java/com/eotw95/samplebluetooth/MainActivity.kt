package com.eotw95.samplebluetooth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.eotw95.samplebluetooth.ui.theme.SampleBluetoothTheme

class MainActivity : ComponentActivity() {
    private lateinit var requestBtPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var enableBtLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestBtScanPermission: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleBluetoothTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {}
            }
        }
    }
}