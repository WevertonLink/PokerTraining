package com.pokertrainer.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pokertrainer.navigation.Screen
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun BottomPillBar(currentRoute: String?, navController: NavController) {
    val items = listOf(
        Screen.Home to Icons.Default.Home,
        Screen.Schedule to Icons.Default.CalendarToday,
        Screen.Gamification to Icons.Default.EmojiEvents
    )

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(Tokens.PillRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.Pill).copy(alpha = 0.88f)
        ),
        border = BorderStroke(1.dp, colorFromHex(Tokens.Border))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { (screen, icon) ->
                val selected = currentRoute == screen.route
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { navController.navigate(screen.route) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icon,
                            contentDescription = screen.route,
                            tint = if (selected) colorFromHex(Tokens.Primary) else Color.White
                        )
                        Text(
                            text = when (screen) {
                                Screen.Home -> "Início"
                                else -> "Agenda"
                            },
                            color = if (selected) colorFromHex(Tokens.Primary) else Color.White,
                            fontSize = Tokens.Caption
                        )
                    }
                }
            }
        }
    }
}
