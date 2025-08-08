package com.pokertrainer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
fun DateChips(
    dates: List<String>,
    selectedIndex: Int,
    onDateSelected: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing)
    ) {
        itemsIndexed(dates) { index, date ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .background(
                        color = if (isSelected) {
                            colorFromHex(Tokens.Primary)
                        } else {
                            colorFromHex(
                                Tokens.Pill
                            )
                        },
                        shape = RoundedCornerShape(Tokens.PillRadius)
                    )
                    .clickable { onDateSelected(index) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
