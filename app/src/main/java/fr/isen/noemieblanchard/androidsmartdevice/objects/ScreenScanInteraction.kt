package fr.isen.noemieblanchard.androidsmartdevice.objects

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import fr.isen.noemieblanchard.androidsmartdevice.screens.BleDevice
import kotlinx.coroutines.CoroutineScope


class ScreenScanInteraction(private val context: Context) {
    val bluetoothAdapter: BluetoothAdapter? =
        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private val scanHandler = Handler(Looper.getMainLooper())

    private var scanCallback: ScanCallback? = null // Conserver la référence
    var isScanning = false
    var scanProgress = 0f
    val scanDuration = 10_000L // 10 secondes
    var scannedDevices = mutableListOf<BleDevice>()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    fun registerPermissionLauncher(launcher: ActivityResultLauncher<Array<String>>) {
        requestPermissionLauncher = launcher
    }

    fun hasBluetoothPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestBluetoothPermissions() {
        val permissionsToRequest = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH)
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    fun checkBluetoothAndStartScan(startScan: () -> Unit) {
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth non disponible", Toast.LENGTH_LONG).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(context, "Veuillez activer le Bluetooth", Toast.LENGTH_LONG).show()
            return
        }

        if (hasBluetoothPermission()) {
            startScan()
        } else {
            requestBluetoothPermissions()
        }
    }


    fun requestPermissionsAtStart(onPermissionsGranted: () -> Unit) {
        if (hasBluetoothPermission()) {
            onPermissionsGranted()
        } else {
            val permissionsToRequest = mutableListOf<String>()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
    fun startBleScan(coroutineScope: CoroutineScope, updateDevices: (List<BleDevice>) -> Unit) {
        if (!hasBluetoothPermission()) {
            requestBluetoothPermissions()
            return
        }

        try {
            scannedDevices.clear()
            isScanning = true
            scanProgress = 0f

            scanCallback = object : ScanCallback() { // Initialiser le callback
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    result?.device?.let { device ->
                        val newDevice = BleDevice(result.rssi.toString(), device.name ?: "Unknown", device.address)
                        if (scannedDevices.none { it.macAddress == newDevice.macAddress }) {
                            scannedDevices.add(newDevice)
                            updateDevices(scannedDevices)
                            Log.d("BLE_SCAN", "Device found: ${newDevice.name}, ${newDevice.macAddress}, RSSI: ${newDevice.signal}")
                        }
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Log.e("BLE_SCAN", "Scan failed with error code: $errorCode")
                    Toast.makeText(context, "Erreur Scan BLE: $errorCode", Toast.LENGTH_SHORT).show()
                    stopBleScan()
                }
            }

            bluetoothLeScanner?.startScan(null, ScanSettings.Builder().build(), scanCallback)

            scanHandler.postDelayed({ stopBleScan() }, scanDuration)
        } catch (e: SecurityException) {
            Log.e("BLE_SCAN", "Security exception: ${e.message}")
            Toast.makeText(context, "Permission Bluetooth requise", Toast.LENGTH_SHORT).show()
            stopBleScan()
        }
    }

    fun stopBleScan() {
        if (isScanning) {
            try {
                bluetoothLeScanner?.stopScan(scanCallback ?: object : ScanCallback(){}) //Utilise le callback stocké
                scanCallback = null // Nettoyer la référence
                isScanning = false
                Toast.makeText(context, "Scan terminé", Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                Log.e("BLE_SCAN", "Erreur: Permissions Bluetooth insuffisantes: ${e.message}")
                Toast.makeText(context, "Erreur: Permissions Bluetooth insuffisantes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}