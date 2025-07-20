package com.example.bluetoothmessenger.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bluetoothmessenger.domain.chat.BluetoothDevice
import com.example.bluetoothmessenger.domain.chat.BluetoothMessage
import com.example.bluetoothmessenger.domain.chat.ChatMessage
import com.example.bluetoothmessenger.ui.theme.Plum



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    state: BluetoothUIState, onDisconnect: () -> Unit, onSendMessage: (String) -> Unit
) {
    val message = rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Plum
                ),
                title = {
                    Text(
                        text = "Offline Messenger",
                        fontWeight = FontWeight.W500
                    )
                },
                actions = {
                    IconButton(
                        onClick = onDisconnect
                    ) {
                        Icon(
                            modifier = Modifier.size(36.dp),
                            imageVector = Icons.Default.Close, contentDescription = "Disconnect"
                        )

                    }
                }
            )
        }
    ) { innerpadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)

        ) {


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                items(state.messages) { message ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ChatMessage(
                            message = message,
                            modifier = Modifier.align(if (message.isFromLocalUser) Alignment.End else Alignment.Start)
                        )

                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),

                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            text = "Message"
                        )
                    },
                    maxLines = 5,
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                onSendMessage(message.value)
                                message.value = ""
                                keyboardController?.hide()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Message"
                            )
                        }
                    }
                )


            }
        }

    }

}