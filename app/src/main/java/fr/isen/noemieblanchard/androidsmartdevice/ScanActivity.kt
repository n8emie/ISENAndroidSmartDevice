package fr.isen.noemieblanchard.androidsmartdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isen.noemieblanchard.androidsmartdevice.screens.ScanScreen
import fr.isen.noemieblanchard.androidsmartdevice.ui.theme.AndroidSmartDeviceTheme
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

