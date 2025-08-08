package com.pokertrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pokertrainer.data.User
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient

@Composable
fun HomeScreen(
    user: User,
    onNavigateToStats: () -> Unit = {},
    onNavigateToPractice: () -> Unit = {},
    onNavigateToAnalysis: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToTutorials: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        TopGreetingBar(
            name = user.name,
            unreadNotifications = 3, // TODO: Get from NotificationViewModel
            onNotificationsClick = onNavigateToNotifications
        )
        BalanceCard(user.bankroll)
        ActionPills(
            onPracticeClick = onNavigateToPractice,
            onReviewClick = onNavigateToStats,
            onAnalysisClick = onNavigateToAnalysis,
            onTutorialsClick = onNavigateToTutorials
        )

        Spacer(Modifier.height(Tokens.SectionSpacing))
        SectionHeader("Módulos")
        StatGrid()

        Spacer(Modifier.height(Tokens.SectionSpacing))
        SectionHeader("Atividade")
        ActivityMiniChart(hands = user.handsSolved, delta = user.weeklyDelta)

        // Add some bottom padding for the bottom bar
        Spacer(Modifier.height(100.dp))
    }
}
