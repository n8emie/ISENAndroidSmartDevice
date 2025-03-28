package fr.isen.noemieblanchard.androidsmartdevice.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            color = Color(0xff1b5e20),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            repeat(3) { i ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xffffe0b2),
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        val bulbImage =
                            if (interaction.ledStates[i]) R.drawable.bulb_off else R.drawable.bulb_on
                        Image(
                            painter = painterResource(id = bulbImage),
                            contentDescription = "LED ${i + 1}",
                            modifier = Modifier.weight(1f) // L'image prend un certain espace
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { interaction.toggleLed(i) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff1b5e20)),
                            modifier = Modifier.weight(4f) // Le bouton prend un certain espace
                        ) {
                            Text(if (interaction.ledStates[i]) "Turn Off LED ${i + 1}" else "Turn On LED ${i + 1}")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider()

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    interaction.toggleNotificationsFor(
                        interaction.notifCharButton1,
                        !interaction.isSubscribedButton1.value
                    ) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff1b5e20)),
                ) {
                Text(if (interaction.isSubscribedButton1.value) "Unsubscribe Button 1" else "Subscribe Button 1")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                interaction.toggleNotificationsFor(
                    interaction.notifCharButton3,
                    !interaction.isSubscribedButton3.value
                )
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xff1b5e20))) {
                Text(if (interaction.isSubscribedButton3.value) "Unsubscribe Button 3" else "Subscribe Button 3")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Compteur Bouton 1: ${interaction.counterButton1.value}", fontSize = 16.sp)
            Text("Compteur Bouton 3: ${interaction.counterButton3.value}", fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    interaction.counterButton1.value = 0
                    interaction.counterButton3.value = 0
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffffe0b2))
            ) {
                Text("Réinitialiser les compteurs", color = Color.Black)
            }
        }
    }
}