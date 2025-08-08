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
fun ActionPills(
    onPracticeClick: () -> Unit = {},
    onReviewClick: () -> Unit = {},
    onAnalysisClick: () -> Unit = {},
    onTutorialsClick: () -> Unit = {}
) {
    val actions = listOf(
        "Praticar" to onPracticeClick,
        "Revisão" to onReviewClick,
        "Análise" to onAnalysisClick,
        "Tutoriais" to onTutorialsClick
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing)
    ) {
        actions.forEach { (action, onClick) ->
            Button(
                onClick = onClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(Tokens.PillRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorFromHex(Tokens.Pill)
                )
            ) {
                Text(action, color = Color.White)
            }
        }
    }
}
