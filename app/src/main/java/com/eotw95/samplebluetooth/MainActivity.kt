package com.eotw95.samplebluetooth

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
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
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { scanBleDevice() }) {
                            Text(text = "Scan BLE")
                        }
                    }
                }
            }
        }
    }

    private fun scanBleDevice() {
        val btManager = getSystemService(BluetoothManager::class.java)
        val btScanner = btManager.adapter.bluetoothLeScanner

        if (btManager.adapter != null && btManager.adapter.isEnabled) Log.d("scanBleDevice", "ble adapter is enable")
        else Log.d("scanBleDevice", "ble adapter is not enable")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("scanBleDevice", "BLUETOOTH_SCAN is not granted")
        }

        val bleScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                Log.d("BleScanCallback", "result:$result")
            }
        }
        btScanner.startScan(bleScanCallback)
    }
}