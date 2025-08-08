package com.pokertrainer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pokertrainer.data.SampleData
import com.pokertrainer.ui.theme.Tokens

@Composable
fun StatGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.height(180.dp),
        horizontalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing),
        verticalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing)
    ) {
        items(SampleData.statModules) { stat ->
            StatCard(stat)
        }
    }
}
