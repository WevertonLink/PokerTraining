package com.pokertrainer.ui.components.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun TimerDisplay(
    timeRemaining: Long,
    totalTime: Long,
    modifier: Modifier = Modifier
) {
    val progress = (timeRemaining.toFloat() / totalTime.toFloat()).coerceIn(0f, 1f)
    val color = when {
        progress < 0.25 -> colorFromHex(Tokens.Negative)
        progress < 0.5 -> Color.Yellow
        else -> colorFromHex(Tokens.Positive)
    }

    Box(
        modifier = modifier
            .size(80.dp)
            .background(colorFromHex(Tokens.Pill), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = 4.dp,
            trackColor = colorFromHex(Tokens.Border)
        )
        Text(
            text = "${timeRemaining / 1000}s",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}
