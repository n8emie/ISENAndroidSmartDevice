package fr.isen.noemieblanchard.androidsmartdevice.objects


import android.app.Application

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.noemieblanchard.androidsmartdevice.DeviceControlActivity
import kotlinx.coroutines.launch
import java.util.UUID

class ScreenDeviceInteraction(private val context: Context) {

    private var gatt: BluetoothGatt? = null
    private var ledChar: BluetoothGattCharacteristic? = null
    var notifCharButton1: BluetoothGattCharacteristic? = null
    var notifCharButton3: BluetoothGattCharacteristic? = null

    val ledStates = mutableStateListOf(false, false, false)
    val connectionState = mutableStateOf("Se connecter")

    val counterButton1 = mutableStateOf(0)
    val counterButton3 = mutableStateOf(0)

    val isSubscribedButton1 = mutableStateOf(false)
    val isSubscribedButton3 = mutableStateOf(false)

    private var skipNextNotification1 = false
    private var skipNextNotification3 = false


    fun connectToDevice(address: String, name: String) {
        connectionState.value = "En cours de connexion"
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val device = bluetoothAdapter.getRemoteDevice(address)

        try {
            gatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        connectionState.value = "Connecté à $name"
                        gatt.discoverServices()
                    } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                        connectionState.value = "Déconnecté"
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        val service3 = gatt.services.getOrNull(2)
                        val service4 = gatt.services.getOrNull(3)

                        ledChar = service3?.characteristics?.getOrNull(0)
                        notifCharButton1 = service3?.characteristics?.getOrNull(1)
                        notifCharButton3 = service4?.characteristics?.getOrNull(0)

                        Log.d("BLE", "LED char = $ledChar")
                        Log.d("BLE", "Notif bouton 1 = $notifCharButton1")
                        Log.d("BLE", "Notif bouton 3 = $notifCharButton3")

                        notifCharButton1?.let {
                            gatt.setCharacteristicNotification(it, true)
                            val descriptor = it.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                            descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(descriptor)
                        }

                        notifCharButton3?.let {
                            gatt.setCharacteristicNotification(it, true)
                            val descriptor = it.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                            descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            gatt.writeDescriptor(descriptor)
                        }
                    } else {
                        Log.e("BLE", "Service discovery failed with status: $status")
                        connectionState.value = "Erreur de service BLE."
                    }
                }

                override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                    when (characteristic) {
                        notifCharButton1 -> {
                            if (skipNextNotification1) {
                                skipNextNotification1 = false
                                Log.d("BLE", "Notification bouton 1 ignorée")
                                return
                            }
                            val value = characteristic.value.firstOrNull()?.toInt() ?: return
                            counterButton1.value = value
                            Log.d("BLE", "Bouton 1, compteur = $value")
                        }

                        notifCharButton3 -> {
                            if (skipNextNotification3) {
                                skipNextNotification3 = false
                                Log.d("BLE", "Notification bouton 3 ignorée")
                                return
                            }
                            val value = characteristic.value.firstOrNull()?.toInt() ?: return
                            counterButton3.value = value
                            Log.d("BLE", "Bouton 3, compteur = $value")
                        }
                    }
                }
            })
        } catch (securityException: SecurityException) {
            Log.e("BLE", "Security exception during connection: ${securityException.message}")
            connectionState.value = "Erreur sécurité BLE."
        } catch (illegalArgumentException: IllegalArgumentException){
            Log.e("BLE", "Illegal Argument Exception during connection : ${illegalArgumentException.message}")
            connectionState.value = "Adresse BLE invalide"
        } catch (exception : Exception){
            Log.e("BLE", "Unknown exception during connection : ${exception.message}")
            connectionState.value = "Erreur connexion BLE"
        }


        fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            when (characteristic) {
            notifCharButton1 -> {
                if (skipNextNotification1) {
                    skipNextNotification1 = false
                    Log.d("BLE", "Notification bouton 1 ignorée")
                    return
                }
                val value = characteristic.value.firstOrNull()?.toInt() ?: return
                counterButton1.value = value
                Log.d("BLE", "📥 Bouton 1 → compteur = $value")
            }

            notifCharButton3 -> {
                if (skipNextNotification3) {
                    skipNextNotification3 = false
                    Log.d("BLE", "Notification bouton 3 ignorée")
                    return
                }
                val value = characteristic.value.firstOrNull()?.toInt() ?: return
                counterButton3.value = value
                Log.d("BLE", " Bouton 3 → compteur = $value")
            }
        }
    }
}



    fun toggleLed(index: Int) {
        val char = ledChar ?: run {
            Log.e("BLE", "LED characteristic is null.")
            return
        }

        if (index < 0 || index >= ledStates.size) {
            Log.e("BLE", "Invalid LED index: $index")
            return
        }

        val alreadyOn = ledStates[index]

        try {
            for (i in ledStates.indices) {
                ledStates[i] = false
            }

            val valueToSend = if (alreadyOn) 0x00 else (index + 1)
            char.value = byteArrayOf(valueToSend.toByte())

            val success = gatt?.writeCharacteristic(char) ?: false

            if(success){
                if (!alreadyOn) {
                    ledStates[index] = true
                }
            } else {
                Log.e("BLE", "Failed to write LED characteristic.")
            }

        } catch (e: SecurityException) {
            Log.e("BLE", "Security exception during writing LED characteristic: ${e.message}")
        } catch (e: IllegalArgumentException) {
            Log.e("BLE", "Illegal Argument exception during writing LED characteristic: ${e.message}")
        } catch (e: Exception) {
            Log.e("BLE", "Error writing LED characteristic: ${e.message}")
        }
    }


    fun toggleNotificationsFor(
        characteristic: BluetoothGattCharacteristic?,
        enable: Boolean
    ) {
        if (characteristic == null) {
            Log.e("BLE", "Characteristic is null, cannot toggle notifications.")
            return
        }

        try {
            gatt?.setCharacteristicNotification(characteristic, enable)

            val descriptor = characteristic.descriptors.firstOrNull()?.let {
                characteristic.getDescriptor(it.uuid)
            } ?: run {
                Log.e("BLE", "No descriptor found for characteristic.")
                return
            }

            descriptor.value = if (enable)
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            else
                BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE

            val success = gatt?.writeDescriptor(descriptor) ?: false

            if(success){
                when (characteristic) {
                    notifCharButton1 -> {
                        isSubscribedButton1.value = enable
                        if (enable) skipNextNotification1 = true
                    }
                    notifCharButton3 -> {
                        isSubscribedButton3.value = enable
                        if (enable) skipNextNotification3 = true
                    }
                }
            } else {
                Log.e("BLE", "Failed to write descriptor.")
            }

        } catch (e: SecurityException) {
            Log.e("BLE", "Security exception during notification toggle: ${e.message}")
        } catch (e: IllegalArgumentException) {
            Log.e("BLE", "Illegal argument exception during notification toggle: ${e.message}")
        } catch (e: Exception) {
            Log.e("BLE", "Error toggling notifications: ${e.message}")
        }
    }

}