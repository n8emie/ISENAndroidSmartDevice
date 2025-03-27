package fr.isen.noemieblanchard.androidsmartdevice.screens

import android.annotation.SuppressLint
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.noemieblanchard.androidsmartdevice.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import fr.isen.noemieblanchard.androidsmartdevice.objects.ScreenScanInteraction
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log



data class FakeBleDevice(val signal: String, val name: String, val macAddress: String)


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ScanScreen(interaction: ScreenScanInteraction) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    var scannedDevices by remember { mutableStateOf(emptyList<FakeBleDevice>()) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            interaction.startBleScan(coroutineScope) { devices ->
                scannedDevices = devices
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color(0xFF1976D2)),
            contentAlignment = Alignment.Center
        ) {
            Text(context.getString(R.string.app_name), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isScanning) "Scan BLE en cours ..." else "Lancer le Scan BLE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(id = if (isScanning) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = "Start Scan",
                tint = Color.Gray,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        isScanning = !isScanning
                        if (!isScanning) interaction.stopBleScan()
                    }
            )
        }

        Divider()

        Column(modifier = Modifier.padding(16.dp)) {
            scannedDevices.forEach { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF90CAF9),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = device.name, color = Color.White, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = device.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = device.macAddress, fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}