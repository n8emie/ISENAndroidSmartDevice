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

        val screenInteraction = ScreenDeviceInteraction(this)

        enableEdgeToEdge()
        setContent {
            DeviceControlScreen(screenInteraction)
        }
    }


}


