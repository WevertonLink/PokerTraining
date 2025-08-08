package com.pokertrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun BalanceCard(bankroll: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(Tokens.CardElevation)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Bankroll",
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral)
            )
            Text(
                text = "$${"%.2f".format(bankroll)}",
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )
        }
    }
}
