package fr.isen.noemieblanchard.androidsmartdevice.objects

import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.MutableState

data class BleDevice(val signal: MutableState<String>,
                     val name: String,
                     val macAddress: String,
                     val bluetoothDevice: BluetoothDevice? = null)
