package fr.isen.noemieblanchard.androidsmartdevice.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
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
import android.content.Intent
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.isen.noemieblanchard.androidsmartdevice.DeviceControlActivity
import fr.isen.noemieblanchard.androidsmartdevice.MainActivity
import fr.isen.noemieblanchard.androidsmartdevice.ScanActivity
import fr.isen.noemieblanchard.androidsmartdevice.objects.BleDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ScanScreen(interaction: ScreenScanInteraction) {

    val context = LocalContext.current
    val activityContext = LocalContext.current as? Activity
    val coroutineScope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    var scannedDevices by remember { mutableStateOf(emptyList<BleDevice>()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            interaction.checkBluetoothAndStartScan { isScanning = true }
        }else {
            activityContext?.finish()
            activityContext?.startActivity(Intent(context, MainActivity::class.java))
        }
    }
    LaunchedEffect(Unit) {
        interaction.registerPermissionLauncher(permissionLauncher)
    }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        activityContext?.finish()
        activityContext?.startActivity(Intent(context, MainActivity::class.java))
    }


    LaunchedEffect(isScanning) {
        if (isScanning) {
            interaction.startBleScan(coroutineScope) { devices ->
                scannedDevices = devices.map{BleDevice(it.signal, it.name, it.macAddress)}
            }
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color(0xff1b5e20)),
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
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        if (isScanning) {
                            interaction.stopBleScan()
                            isScanning = false
                        } else {
                            interaction.requestPermissionsAtStart {
                                if (interaction.bluetoothAdapter?.isEnabled == false) {
                                    enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                                } else {
                                    interaction.checkBluetoothAndStartScan {
                                        isScanning = true
                                    }
                                }
                            }
                        }
                    }
            )
            Icon(
                painter = painterResource(id = if (isScanning) R.drawable.ic_pause else R.drawable.ic_play),
                contentDescription = "Start Scan",
                tint = Color.Gray,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        if (isScanning) {
                            interaction.stopBleScan()
                            isScanning = false
                        } else {
                            interaction.requestPermissionsAtStart {
                                if (interaction.bluetoothAdapter?.isEnabled == false) {
                                    enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                                } else {
                                    interaction.checkBluetoothAndStartScan {
                                        isScanning = true
                                    }
                                }
                            }
                        }
                    }
            )
        }

        Divider()

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(scannedDevices) { device ->
                DeviceCard(
                    device = device,
                    onClick = {
                        val intent = Intent(context, DeviceControlActivity::class.java).apply {
                        putExtra("DEVICE_ADDRESS", device.macAddress)
                        putExtra("DEVICE_NAME", device.name)
                    }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun DeviceCard(device: BleDevice,  onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable{ onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xffffe0b2),
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xff1b5e20),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = device.signal.value, color = Color.White, fontSize = 14.sp)
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