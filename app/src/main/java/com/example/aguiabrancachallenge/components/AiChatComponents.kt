package com.example.aguiabrancachallenge.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aguiabrancachallenge.operador.BrandBlue // Certifique-se de que BrandBlue está acessível aqui

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun ChatBubble(msg: ChatMessage) {
    val backgroundColor = if (msg.isUser) BrandBlue else Color(0xFF16181D)
    val textColor = if (msg.isUser) Color.White else Color(0xFFE0E0E0)
    val alignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (msg.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(shape)
                .background(backgroundColor)
                .padding(12.dp)
        ) {
            Text(
                text = msg.text,
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}