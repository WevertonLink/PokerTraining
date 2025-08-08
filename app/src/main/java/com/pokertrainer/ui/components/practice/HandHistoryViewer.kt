package com.pokertrainer.ui.components.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

data class HandAction(
    val player: String,
    val action: String,
    val amount: Double? = null,
    val position: String
)

data class HandHistory(
    val handId: String,
    val blinds: String,
    val heroPosition: String,
    val heroCards: String,
    val preflopActions: List<HandAction>,
    val flopCards: String? = null,
    val flopActions: List<HandAction> = emptyList(),
    val turnCard: String? = null,
    val turnActions: List<HandAction> = emptyList(),
    val riverCard: String? = null,
    val riverActions: List<HandAction> = emptyList(),
    val result: String
)

@Composable
fun HandHistoryViewer(
    handHistory: HandHistory,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header com informações da mão
            HandHistoryHeader(handHistory)

            Spacer(modifier = Modifier.height(16.dp))

            // Ações da mão por street
            HandHistoryStreets(handHistory)

            Spacer(modifier = Modifier.height(16.dp))

            // Resultado
            HandResult(handHistory.result)
        }
    }
}

@Composable
private fun HandHistoryHeader(handHistory: HandHistory) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hand #${handHistory.handId}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = handHistory.blinds,
                fontSize = 12.sp,
                color = colorFromHex(Tokens.Neutral)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Hero: ${handHistory.heroPosition}",
                fontSize = 12.sp,
                color = colorFromHex(Tokens.Neutral)
            )
            Text(
                text = handHistory.heroCards,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorFromHex(Tokens.Primary)
            )
        }
    }
}

@Composable
private fun HandHistoryStreets(handHistory: HandHistory) {
    LazyColumn(
        modifier = Modifier.heightIn(max = 300.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Preflop
        item {
            StreetSection(
                title = "Preflop",
                actions = handHistory.preflopActions
            )
        }

        // Flop
        handHistory.flopCards?.let { flopCards ->
            item {
                StreetSection(
                    title = "Flop",
                    boardCards = flopCards,
                    actions = handHistory.flopActions
                )
            }
        }

        // Turn
        handHistory.turnCard?.let { turnCard ->
            item {
                StreetSection(
                    title = "Turn",
                    boardCards = "${handHistory.flopCards} $turnCard",
                    actions = handHistory.turnActions
                )
            }
        }

        // River
        handHistory.riverCard?.let { riverCard ->
            item {
                StreetSection(
                    title = "River",
                    boardCards = "${handHistory.flopCards} ${handHistory.turnCard} $riverCard",
                    actions = handHistory.riverActions
                )
            }
        }
    }
}

@Composable
private fun StreetSection(
    title: String,
    boardCards: String? = null,
    actions: List<HandAction>
) {
    Column {
        // Street header
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = colorFromHex(Tokens.Primary)
            )

            boardCards?.let { cards ->
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "[$cards]",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = colorFromHex(Tokens.Pill),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Actions
        actions.forEach { action ->
            ActionRow(action)
        }
    }
}

@Composable
private fun ActionRow(action: HandAction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Text(
                text = action.position,
                fontSize = 11.sp,
                color = colorFromHex(Tokens.Neutral),
                modifier = Modifier.width(40.dp)
            )
            Text(
                text = action.player,
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.width(80.dp)
            )
        }

        Row {
            Text(
                text = action.action,
                fontSize = 12.sp,
                color = when (action.action.lowercase()) {
                    "fold" -> colorFromHex(Tokens.Negative)
                    "call" -> colorFromHex(Tokens.Neutral)
                    "raise", "bet" -> colorFromHex(Tokens.Positive)
                    "check" -> colorFromHex(Tokens.Neutral)
                    else -> Color.White
                }
            )

            action.amount?.let { amount ->
                Text(
                    text = " $$amount",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HandResult(result: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorFromHex(Tokens.Pill),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = colorFromHex(Tokens.Border),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = result,
            fontSize = 12.sp,
            color = if (result.contains("+")) {
                colorFromHex(Tokens.Positive)
            } else {
                colorFromHex(Tokens.Negative)
            },
            fontWeight = FontWeight.Bold
        )
    }
}
