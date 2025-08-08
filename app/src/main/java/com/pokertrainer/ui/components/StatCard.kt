package com.pokertrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokertrainer.data.StatModule
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun StatCard(stat: StatModule) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stat.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorFromHex(Tokens.Neutral)
                )
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            LinearProgressIndicator(
                progress = stat.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = colorFromHex(stat.color),
                trackColor = colorFromHex(Tokens.Border)
            )
        }
    }
}
