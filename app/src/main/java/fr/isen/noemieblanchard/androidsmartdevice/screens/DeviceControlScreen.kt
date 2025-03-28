package fr.isen.noemieblanchard.androidsmartdevice.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.noemieblanchard.androidsmartdevice.R
import fr.isen.noemieblanchard.androidsmartdevice.objects.ScreenDeviceInteraction


@Composable
fun DeviceControlScreen(interaction: ScreenDeviceInteraction) {

    val context = LocalContext.current


    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color(0xff1b5e20)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                context.getString(R.string.app_name),
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Status: ${interaction.connectionState.value}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        for (i in 0..2) {
            Image(
                painter = painterResource(if (interaction.ledStates[i]) R.drawable.bulb_off else R.drawable.bulb_on),
                contentDescription = "LED ${i + 1}"
            )
            Button(onClick = { interaction.toggleLed(i) }) {
                Text(if (interaction.ledStates[i]) "Turn Off LED ${i + 1}" else "Turn On LED ${i + 1}")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { interaction.toggleNotificationsFor(interaction.notifCharButton1, !interaction.isSubscribedButton1.value) }) {
            Text(if (interaction.isSubscribedButton1.value) "Unsubscribe Button 1" else "Subscribe Button 1")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { interaction.toggleNotificationsFor(interaction.notifCharButton3, !interaction.isSubscribedButton3.value) }) {
            Text(if (interaction.isSubscribedButton3.value) "Unsubscribe Button 3" else "Subscribe Button 3")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Button 1 Presses: ${interaction.counterButton1.value}", fontSize = 16.sp)
        Text("Button 3 Presses: ${interaction.counterButton3.value}", fontSize = 16.sp)
    }

}