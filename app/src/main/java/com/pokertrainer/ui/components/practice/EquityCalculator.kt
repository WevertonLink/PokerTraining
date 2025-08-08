package com.pokertrainer.ui.components.practice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun ProgressIndicator(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { current.toFloat() / total.toFloat() },
            modifier = Modifier.size(60.dp),
            color = colorFromHex(Tokens.Primary),
            strokeWidth = 6.dp,
            trackColor = colorFromHex(Tokens.Border)
        )
        Text(
            text = "$current/$total",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
fun EquityCalculator(
    equity: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Equity",
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral)
            )
            Text(
                text = "${(equity * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = colorFromHex(Tokens.Primary)
            )
        }
    }
}
