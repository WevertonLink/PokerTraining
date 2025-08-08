package com.pokertrainer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DarkColorScheme = darkColorScheme(
    primary = colorFromHex(Tokens.Primary),
    onPrimary = Color.White,
    surface = colorFromHex(Tokens.Surface),
    surfaceVariant = colorFromHex(Tokens.SurfaceElevated),
    onSurface = Color.White,
    background = colorFromHex(Tokens.BackgroundTop),
    onBackground = Color.White
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = Tokens.DisplayXL,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = Tokens.TitleL,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = Tokens.TitleM,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = Tokens.Body,
        lineHeight = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = Tokens.Caption,
        lineHeight = 16.sp
    )
)

@Composable
fun PokerTrainerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

fun backgroundGradient() = Brush.verticalGradient(
    colors = listOf(
        colorFromHex(Tokens.BackgroundTop),
        colorFromHex(Tokens.BackgroundBottom)
    )
)

fun colorFromHex(hex: String) = Color(android.graphics.Color.parseColor(hex))
