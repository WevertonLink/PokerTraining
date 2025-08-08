package com.pokertrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun ActivityMiniChart(hands: Int, delta: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mãos resolvidas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    Text(
                        text = hands.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Esta semana",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    Text(
                        text = "+$delta",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorFromHex(Tokens.Positive)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mini chart representation with bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val heights = listOf(0.3f, 0.7f, 0.5f, 0.9f, 0.6f, 0.8f, 1.0f)
                heights.forEach { height ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height((40 * height).dp)
                            .fillMaxWidth()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = colorFromHex(Tokens.Primary).copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(2.dp)
                        ) {}
                    }
                }
            }
        }
    }
}
