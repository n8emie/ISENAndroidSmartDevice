package fr.isen.noemieblanchard.androidsmartdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fr.isen.noemieblanchard.androidsmartdevice.screens.ScanScreen
import fr.isen.noemieblanchard.androidsmartdevice.objects.ScreenScanInteraction

class ScanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val screenInteraction = ScreenScanInteraction(this)
        setContent {
            ScanScreen(screenInteraction)
        }
    }
}

