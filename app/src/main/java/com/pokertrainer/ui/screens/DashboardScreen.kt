package com.pokertrainer.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.*
import com.pokertrainer.ui.viewmodels.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToTutorials: () -> Unit = {},
    onNavigateToPractice: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToAnalysis: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCustomizeDialog by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient())
        ) {
            // Enhanced Header with User Info
            DashboardHeader(
                user = uiState.user,
                weatherInfo = uiState.weatherInfo,
                onNotificationsClick = onNavigateToNotifications,
                onRefresh = { viewModel.refreshDashboard() },
                onCustomize = { showCustomizeDialog = true }
            )
            
            // Dashboard Content
            if (uiState.isLoading) {
                DashboardSkeleton()
            } else {
                DashboardContent(
                    uiState = uiState,
                    onNavigateToPractice = onNavigateToPractice,
                    onNavigateToStats = onNavigateToStats,
                    onNavigateToAnalysis = onNavigateToAnalysis,
                    onCompleteGoal = { viewModel.completeGoal(it) }
                )
            }
        }
        
        // Customize Dialog
        if (showCustomizeDialog) {
            CustomizeDashboardDialog(
                widgets = uiState.widgets,
                onDismiss = { showCustomizeDialog = false },
                onToggleWidget = { viewModel.toggleWidget(it) },
                onReorderWidgets = { from, to -> viewModel.reorderWidgets(from, to) }
            )
        }
    }
}

@Composable
private fun DashboardHeader(
    user: DashboardUser,
    weatherInfo: WeatherInfo?,
    onNotificationsClick: () -> Unit,
    onRefresh: () -> Unit,
    onCustomize: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Top row with greeting and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.avatar,
                            fontSize = 24.sp
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = getGreeting(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorFromHex(Tokens.Neutral)
                            )
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Row {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar",
                            tint = Color.White
                        )
                    }
                    
                    IconButton(onClick = onCustomize) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Personalizar",
                            tint = Color.White
                        )
                    }
                    
                    IconButton(onClick = onNotificationsClick) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificações",
                                tint = Color.White
                            )
                            
                            // Notification badge
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = colorFromHex(Tokens.Error),
                                        shape = CircleShape
                                    )
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                UserStatItem(
                    label = "Level",
                    value = user.level.toString(),
                    icon = "🎯"
                )
                UserStatItem(
                    label = "Bankroll",
                    value = "$${String.format("%.0f", user.bankroll)}",
                    icon = "💰"
                )
                UserStatItem(
                    label = "Win Rate",
                    value = "${user.winRate}%",
                    icon = "📈"
                )
                UserStatItem(
                    label = "Streak",
                    value = "${user.currentStreak}d",
                    icon = "🔥"
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // XP Progress Bar
            XPProgressBar(
                currentXP = user.currentXP,
                nextLevelXP = user.nextLevelXP,
                level = user.level
            )
            
            // Weather info if available
            weatherInfo?.let { weather ->
                Spacer(modifier = Modifier.height(12.dp))
                WeatherCard(weather)
            }
        }
    }
}

@Composable
private fun UserStatItem(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorFromHex(Tokens.Neutral)
        )
    }
}

@Composable
private fun XPProgressBar(
    currentXP: Int,
    nextLevelXP: Int,
    level: Int
) {
    val progress = currentXP.toFloat() / nextLevelXP.toFloat()
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progresso para Level ${level + 1}",
                style = MaterialTheme.typography.labelMedium,
                color = colorFromHex(Tokens.Neutral)
            )
            Text(
                text = "$currentXP / $nextLevelXP XP",
                style = MaterialTheme.typography.labelSmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = colorFromHex(Tokens.Primary),
            trackColor = colorFromHex(Tokens.Neutral).copy(alpha = 0.3f)
        )
    }
}

@Composable
private fun WeatherCard(weather: WeatherInfo) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.Primary).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = weather.emoji,
                fontSize = 20.sp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${weather.temperature}°C • ${weather.condition}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = weather.suggestion,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorFromHex(Tokens.Neutral),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onNavigateToPractice: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToAnalysis: () -> Unit,
    onCompleteGoal: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Stats Widget
        item {
            QuickStatsWidget(
                stats = uiState.quickStats,
                onNavigateToStats = onNavigateToStats
            )
        }
        
        // Daily Goals Widget
        item {
            DailyGoalsWidget(
                goals = uiState.dailyGoals,
                onCompleteGoal = onCompleteGoal
            )
        }
        
        // Smart Recommendations Widget
        item {
            SmartRecommendationsWidget(
                recommendations = uiState.recommendations,
                onNavigateToPractice = onNavigateToPractice
            )
        }
        
        // Recent Activity Widget
        item {
            RecentActivityWidget(
                activities = uiState.recentActivity
            )
        }
        
        // Upcoming Sessions Widget
        if (uiState.upcomingSessions.isNotEmpty()) {
            item {
                UpcomingSessionsWidget(
                    sessions = uiState.upcomingSessions
                )
            }
        }
        
        // Recent Achievements Widget
        if (uiState.achievements.isNotEmpty()) {
            item {
                RecentAchievementsWidget(
                    achievements = uiState.achievements
                )
            }
        }
        
        // Bottom spacing for navigation bar
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun QuickStatsWidget(
    stats: QuickStats,
    onNavigateToStats: () -> Unit
) {
    DashboardWidgetCard(
        title = "📊 Estatísticas de Hoje",
        onNavigate = onNavigateToStats
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatQuickItem(
                label = "Mãos",
                value = stats.todayHands.toString(),
                color = colorFromHex(Tokens.Primary)
            )
            StatQuickItem(
                label = "XP Ganho",
                value = stats.todayXP.toString(),
                color = colorFromHex(Tokens.Warning)
            )
            StatQuickItem(
                label = "Precisão",
                value = "${stats.weeklyAccuracy}%",
                color = colorFromHex(Tokens.Positive)
            )
            StatQuickItem(
                label = "Posição",
                value = "#${stats.currentPosition}",
                color = colorFromHex(Tokens.Primary)
            )
        }
    }
}

@Composable
private fun StatQuickItem(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorFromHex(Tokens.Neutral)
        )
    }
}

@Composable
private fun DailyGoalsWidget(
    goals: List<DailyGoal>,
    onCompleteGoal: (String) -> Unit
) {
    DashboardWidgetCard(title = "🎯 Metas Diárias") {
        goals.forEach { goal ->
            DailyGoalItem(
                goal = goal,
                onComplete = { onCompleteGoal(goal.id) }
            )
            
            if (goal != goals.last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DailyGoalItem(
    goal: DailyGoal,
    onComplete: () -> Unit
) {
    val progress = goal.current.toFloat() / goal.target.toFloat()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "${goal.current}/${goal.target} ${goal.unit}",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (goal.isCompleted) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Primary),
                trackColor = colorFromHex(Tokens.Neutral).copy(alpha = 0.3f)
            )
        }
        
        if (goal.isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Concluído",
                tint = colorFromHex(Tokens.Positive),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp)
            )
        } else if (progress >= 0.8f) {
            IconButton(
                onClick = onComplete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Completar",
                    tint = colorFromHex(Tokens.Primary),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SmartRecommendationsWidget(
    recommendations: List<SmartRecommendation>,
    onNavigateToPractice: () -> Unit
) {
    DashboardWidgetCard(
        title = "🤖 Recomendações IA",
        onNavigate = onNavigateToPractice
    ) {
        recommendations.take(2).forEach { recommendation ->
            SmartRecommendationItem(recommendation = recommendation)
            
            if (recommendation != recommendations.take(2).last()) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun SmartRecommendationItem(recommendation: SmartRecommendation) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recommendation.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Priority badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (recommendation.priority) {
                            RecommendationPriority.CRITICAL -> colorFromHex(Tokens.Error)
                            RecommendationPriority.HIGH -> colorFromHex(Tokens.Warning)
                            RecommendationPriority.MEDIUM -> colorFromHex(Tokens.Primary)
                            RecommendationPriority.LOW -> colorFromHex(Tokens.Neutral)
                        }
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = recommendation.priority.name,
                        fontSize = 8.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            
            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Neutral),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${recommendation.estimatedTime}min",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorFromHex(Tokens.Neutral)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "+${(recommendation.potentialImprovement).toInt()}% melhoria",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorFromHex(Tokens.Positive),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Button(
            onClick = { /* Handle recommendation action */ },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorFromHex(Tokens.Primary)
            ),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = recommendation.actionText,
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun RecentActivityWidget(activities: List<ActivityItem>) {
    DashboardWidgetCard(title = "📈 Atividade Recente") {
        activities.take(3).forEach { activity ->
            ActivityItem(activity = activity)
            
            if (activity != activities.take(3).last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ActivityItem(activity: ActivityItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = activity.icon,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = activity.description,
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            activity.value?.let { value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorFromHex(Tokens.Positive),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = activity.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.labelSmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
private fun UpcomingSessionsWidget(sessions: List<UpcomingSession>) {
    DashboardWidgetCard(title = "📅 Próximas Sessões") {
        sessions.take(2).forEach { session ->
            UpcomingSessionItem(session = session)
            
            if (session != sessions.take(2).last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun UpcomingSessionItem(session: UpcomingSession) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                if (session.isOptimal) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "Horário ótimo",
                        tint = colorFromHex(Tokens.Warning),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
            
            Text(
                text = "${session.type} • ${session.duration}min • ${session.difficulty}",
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
        
        Text(
            text = session.scheduledTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            style = MaterialTheme.typography.labelMedium,
            color = colorFromHex(Tokens.Primary),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecentAchievementsWidget(achievements: List<RecentAchievement>) {
    DashboardWidgetCard(title = "🏆 Conquistas Recentes") {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(achievements) { achievement ->
                AchievementCard(achievement = achievement)
            }
        }
    }
}

@Composable
private fun AchievementCard(achievement: RecentAchievement) {
    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (achievement.rarity) {
                AchievementRarity.COMMON -> colorFromHex(Tokens.Neutral).copy(alpha = 0.1f)
                AchievementRarity.RARE -> colorFromHex(Tokens.Primary).copy(alpha = 0.1f)
                AchievementRarity.EPIC -> colorFromHex(Tokens.Warning).copy(alpha = 0.1f)
                AchievementRarity.LEGENDARY -> colorFromHex("#9C27B0").copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = achievement.icon,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = "+${achievement.xpReward} XP",
                style = MaterialTheme.typography.labelSmall,
                color = colorFromHex(Tokens.Warning),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DashboardWidgetCard(
    title: String,
    onNavigate: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                onNavigate?.let { navigate ->
                    IconButton(
                        onClick = navigate,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Ver mais",
                            tint = colorFromHex(Tokens.Primary),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
private fun DashboardSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(4) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorFromHex(Tokens.SurfaceElevated).copy(alpha = 0.3f)
                )
            ) {
                // Skeleton content would go here
            }
        }
    }
}

@Composable
private fun CustomizeDashboardDialog(
    widgets: List<DashboardWidget>,
    onDismiss: () -> Unit,
    onToggleWidget: (String) -> Unit,
    onReorderWidgets: (Int, Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Personalizar Dashboard",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn {
                items(widgets) { widget ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = widget.title,
                            color = Color.White
                        )
                        
                        Switch(
                            checked = widget.isVisible,
                            onCheckedChange = { onToggleWidget(widget.id) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colorFromHex(Tokens.Primary),
                                checkedTrackColor = colorFromHex(Tokens.Primary).copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorFromHex(Tokens.Primary)
                )
            ) {
                Text("Salvar", color = Color.White)
            }
        },
        containerColor = colorFromHex(Tokens.SurfaceElevated)
    )
}

private fun getGreeting(): String {
    val hour = java.time.LocalTime.now().hour
    return when (hour) {
        in 5..11 -> "Bom dia"
        in 12..17 -> "Boa tarde"
        else -> "Boa noite"
    }
}
