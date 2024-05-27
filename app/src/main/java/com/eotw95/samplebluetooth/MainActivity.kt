package com.eotw95.samplebluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.eotw95.samplebluetooth.ui.theme.SampleBluetoothTheme

class MainActivity : ComponentActivity() {
    private lateinit var requestBtPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var enableBtLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showBluetoothDialog()
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

    private fun showBluetoothDialog() {
        // ① get BluetoothAdapter
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) println("Device doesn't support Bluetooth")

        // BLEがサポートされているデバイスかどうか確認
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            println("BLE is not supported")
        } else println("BLE is supported")

        requestBtPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 許可を選択したら通るルート
                println("デバイス検出を許可")

                if (!bluetoothAdapter.isEnabled) {
                    println("Bluetoothが有効化されていない")
                    // ③ Bluetoothが有効化されていないため、有効化するためのリクエストをユーザーに投げる
                    enableBtLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                } else {
                    // Bluetoothが有効化されている
                    println("Bluetoothが有効化されている")
                }

            } else {
                // 拒否を選択したら通るルート
                println("デバイス検出を拒否")
            }
        }
        enableBtLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) scanDevice() else println("result is ng")
        }

        if (VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // ② bluetoothAdapterがnullでなければ、Bluetoothで付近のデバイスを検出することの許可をユーザーに求めるダイアログの表示
            requestBtPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
        } else requestBtPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH)
    }

    private fun scanDevice() {}
}