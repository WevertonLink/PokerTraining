package com.pokertrainer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun AvatarBubble(
    avatar: String,
    size: Int = 40,
    backgroundColor: String = Tokens.SurfaceElevated
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(colorFromHex(backgroundColor)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = avatar,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AvatarBubbleWithBorder(
    avatar: String,
    size: Int = 40,
    borderColor: String = Tokens.Primary,
    backgroundColor: String = Tokens.SurfaceElevated
) {
    Box(
        modifier = Modifier
            .size((size + 4).dp)
            .clip(CircleShape)
            .background(colorFromHex(borderColor)),
        contentAlignment = Alignment.Center
    ) {
        AvatarBubble(
            avatar = avatar,
            size = size - 4,
            backgroundColor = backgroundColor
        )
    }
}
