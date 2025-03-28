package fr.isen.noemieblanchard.androidsmartdevice


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fr.isen.noemieblanchard.androidsmartdevice.objects.ScreenDeviceInteraction
import fr.isen.noemieblanchard.androidsmartdevice.screens.DeviceControlScreen

class DeviceControlActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceAddress = intent.getStringExtra("DEVICE_ADDRESS")
        val deviceName = intent.getStringExtra("DEVICE_NAME")

        if (deviceAddress == null || deviceName == null) {
            finish()
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


