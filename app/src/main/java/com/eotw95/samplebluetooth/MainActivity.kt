package com.eotw95.samplebluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.eotw95.samplebluetooth.ui.theme.SampleBluetoothTheme

class MainActivity : ComponentActivity() {
    private lateinit var requestBtPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var enableBtLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestBtScanPermission: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBle()
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

    private fun setupBle() {
        // BLEがサポートされているデバイスかどうか確認
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            println("BLE is not supported")
        } else println("BLE is supported")

        // BluetoothAdapterを取得
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) println("Device doesn't support Bluetooth")

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
        requestBtScanPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) println("not permitted BLE scan") else println("not permitted BLE scan")
        }

        if (VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // bluetoothAdapterがnullでなければ、Bluetoothで付近のデバイスを検出することの許可をユーザーに求めるダイアログの表示
            requestBtPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        } else requestBtPermissionLauncher.launch(Manifest.permission.BLUETOOTH)

        // BluetoothScannerを取得
        val bluetoothScanner = bluetoothAdapter.bluetoothLeScanner
        val bleScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                println("onScanResult result=$result")
            }
        }

        // Check if permission of BLE Scan is enabled
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        ) {
            println("BLUETOOTH_SCAN permitted")
        } else {
            println("BLUETOOTH_SCAN not permitted")
            requestBtScanPermission.launch(Manifest.permission.BLUETOOTH_SCAN)
        }

        // startScan ble device
        val handler = Handler()
        var scanning = false
        val SCAN_PERIOD: Long = 10000
        if (!scanning) {
            handler.postDelayed(
                {
                    scanning =  false
                    bluetoothScanner.stopScan(bleScanCallback)
                },
                SCAN_PERIOD
            )
            scanning = true
            bluetoothScanner.startScan(bleScanCallback)
        } else {
            scanning = false
            bluetoothScanner.stopScan(bleScanCallback)
        }
    }

    private fun scanDevice() {}

    private fun buildScanFilters(): List<ScanFilter> {
        val scanFilters = mutableListOf<ScanFilter>()
        val scanFilter = ScanFilter.Builder().setServiceUuid(null).build()
        scanFilters.add(scanFilter)
        return scanFilters
    }
    private fun buildScanSettings(): ScanSettings {
        return ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
    }
}