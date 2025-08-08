package com.pokertrainer.ui.components.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun RangeSelector(
    selectedCombos: Set<String>,
    onComboSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardValues = listOf("A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2")

    Column(modifier = modifier) {
        // Cabeçalho
        Row {
            Spacer(Modifier.weight(1f))
            cardValues.forEach { value ->
                Text(
                    text = value,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    textAlign = TextAlign.Center,
                    color = colorFromHex(Tokens.Neutral),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // Matriz de seleção
        cardValues.forEachIndexed { rowIndex, rowValue ->
            Row {
                Text(
                    text = rowValue,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    color = colorFromHex(Tokens.Neutral),
                    style = MaterialTheme.typography.labelSmall
                )
                cardValues.forEachIndexed { colIndex, colValue ->
                    val combo = if (rowIndex <= colIndex) {
                        if (rowIndex == colIndex) {
                            "$rowValue$colValue" // Pairs
                        } else {
                            "$rowValue${colValue}s" // Suited
                        }
                    } else {
                        "$colValue${rowValue}o" // Offsuit
                    }

                    val isSelected = selectedCombos.contains(combo)

                    // Determine background color based on combo type
                    val backgroundColor = when {
                        isSelected -> colorFromHex(Tokens.Primary)
                        rowIndex == colIndex -> colorFromHex(Tokens.SurfaceElevated) // Pairs
                        rowIndex < colIndex -> colorFromHex(Tokens.Surface) // Suited
                        else -> colorFromHex(Tokens.Pill) // Offsuit
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = colorFromHex(Tokens.Border),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onComboSelected(combo) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        } else {
                            Text(
                                text = when {
                                    rowIndex == colIndex -> rowValue // Show single card for pairs
                                    rowIndex < colIndex -> "s" // Show 's' for suited
                                    else -> "o" // Show 'o' for offsuit
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontSize = 8.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
