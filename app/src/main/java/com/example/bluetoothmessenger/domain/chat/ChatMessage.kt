package com.example.bluetoothmessenger.domain.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bluetoothmessenger.ui.theme.BluetoothMessengerTheme
import com.example.bluetoothmessenger.ui.theme.OldRose
import com.example.bluetoothmessenger.ui.theme.Vanilla

@Composable
fun ChatMessage(
    message: BluetoothMessage,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .clip(
                shape = RoundedCornerShape(
                    topStart = if (message.isFromLocalUser) 16.dp else 0.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = if(message.isFromLocalUser) 0.dp else 16.dp
                )
                )
            .background(
                if(message.isFromLocalUser) OldRose else Vanilla
            )
            .padding(16.dp)
    ) {
        Text(
            text = message.senderName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = message.message,
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier.widthIn(max = 240.dp)
        )
    }
}

@Preview
@Composable
fun ChatMessagePreview() {
    BluetoothMessengerTheme {
        ChatMessage(
            message = BluetoothMessage(
                message = "Hello world",
                senderName = "Pixedl 8",
                isFromLocalUser = true
            )
        )
    }
}