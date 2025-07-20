package com.example.bluetoothmessenger.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetoothmessenger.domain.chat.BluetoothDevice
import com.example.bluetoothmessenger.ui.theme.LightBlue
import com.example.bluetoothmessenger.ui.theme.Violet


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun show() {
    DeviceScreen(
        state = BluetoothUIState(
            scannedDevices = listOf(
                BluetoothDevice(
                    name = "Samsung F22",
                    address = "Address"
                ),
                BluetoothDevice(
                    name = "Unknown",
                    address = ""
                )
            ),
            pairedDevices = listOf(
                BluetoothDevice("TF 112", "lafsd"),
                BluetoothDevice("Narzo 30a", "lafsd"),
                BluetoothDevice("Redmi 9", "lafsd"),
                BluetoothDevice("Redmi 7", "lafsd"),
                BluetoothDevice("Redmi 3a", "lafsd")
            )
        ),
        onStartScan = {},
        onStopScan = {},
        onDeviceClick = {},
        onStartServer = {}
    )
}

@Composable
fun DeviceScreen(
    state: BluetoothUIState,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onDeviceClick: (BluetoothDevice) -> Unit,
    onStartServer: () -> Unit
) {
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            BluetoothDeviceList(
                pairedDevices = state.pairedDevices,
                scannedDevices = state.scannedDevices,
                onClick = onDeviceClick,
                modifier = Modifier
                    .fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = onStartScan,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBlue,
                        contentColor = Violet
                    )
                ) {
                    Text(
                        text = "Start Scan"
                    )
                }
                Button(
                    onClick = onStopScan,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBlue,
                        contentColor = Violet
                    )
                ) {
                    Text(
                        text = "Stop Scan"
                    )
                }
                Button(
                    onClick = onStartServer,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBlue,
                        contentColor = Violet
                    )
                ) {
                    Text(
                        text = "Start Server"
                    )
                }

            }

        }
    }

}

@Composable
fun BluetoothDeviceList(
    pairedDevices: List<BluetoothDevice>,
    scannedDevices: List<BluetoothDevice>,
    onClick: (BluetoothDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        item {
            Text(
                text = "Paired devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(pairedDevices) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = it.name ?: "Un known",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    fontSize = 16.sp
                )
                TextButton(
                    onClick = { onClick(it) }
                ) {
                    Text(
                        text = "Chat now"
                    )
                }
            }
        }
        item {
            Text(
                text = "Scanned devices",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
        items(scannedDevices) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = it.name ?: "Un known",
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )
                TextButton(
                    onClick = { onClick(it) }
                ) {
                    Text(
                        text = "Connect now"
                    )
                }
            }
        }

    }

}