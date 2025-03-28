package fr.isen.noemieblanchard.androidsmartdevice

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import fr.isen.noemieblanchard.androidsmartdevice.objects.ScreenDeviceInteraction
import fr.isen.noemieblanchard.androidsmartdevice.objects.ScreenScanInteraction
import fr.isen.noemieblanchard.androidsmartdevice.screens.DeviceControlScreen

class DeviceControlActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceAddress = intent.getStringExtra("DEVICE_ADDRESS")
        val deviceName = intent.getStringExtra("DEVICE_NAME")

        if (deviceAddress == null || deviceName == null) {
            finish() // Close if no device is provided
            return
        }

        val screenInteraction = ScreenDeviceInteraction(this)
        screenInteraction.connectToDevice(deviceAddress, deviceName)

        enableEdgeToEdge()
        setContent {
            DeviceControlScreen(screenInteraction)
        }
    }


}


