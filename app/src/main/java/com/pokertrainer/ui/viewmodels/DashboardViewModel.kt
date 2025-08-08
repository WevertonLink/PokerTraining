package com.pokertrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class DashboardUiState(
    val user: DashboardUser = DashboardUser(),
    val widgets: List<DashboardWidget> = emptyList(),
    val quickStats: QuickStats = QuickStats(),
    val recentActivity: List<ActivityItem> = emptyList(),
    val recommendations: List<SmartRecommendation> = emptyList(),
    val dailyGoals: List<DailyGoal> = emptyList(),
    val upcomingSessions: List<UpcomingSession> = emptyList(),
    val achievements: List<RecentAchievement> = emptyList(),
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val lastUpdated: LocalDateTime = LocalDateTime.now()
)

data class DashboardUser(
    val name: String = "Alex",
    val level: Int = 13,
    val currentXP: Int = 2450,
    val nextLevelXP: Int = 3000,
    val bankroll: Double = 12580.50,
    val winRate: Double = 67.3,
    val currentStreak: Int = 7,
    val totalHands: Int = 1247,
    val rankPosition: Int = 156,
    val avatar: String = "🎯",
    val membership: MembershipTier = MembershipTier.PREMIUM
)

data class DashboardWidget(
    val id: String,
    val type: WidgetType,
    val title: String,
    val data: WidgetData,
    val isVisible: Boolean = true,
    val position: Int = 0,
    val size: WidgetSize = WidgetSize.MEDIUM
)

data class QuickStats(
    val todayHands: Int = 23,
    val todayXP: Int = 340,
    val weeklyAccuracy: Double = 89.5,
    val currentPosition: Int = 156,
    val improvementRate: Double = 12.3,
    val studyTime: Double = 2.5, // hours
    val completedChallenges: Int = 3,
    val activeChallenges: Int = 2
)

data class ActivityItem(
    val id: String,
    val type: ActivityType,
    val title: String,
    val description: String,
    val timestamp: LocalDateTime,
    val value: String? = null,
    val icon: String = "📊"
)

data class SmartRecommendation(
    val id: String,
    val type: RecommendationType,
    val title: String,
    val description: String,
    val priority: RecommendationPriority,
    val estimatedTime: Int, // minutes
    val potentialImprovement: Double,
    val confidence: Double,
    val actionText: String = "Iniciar"
)

data class DailyGoal(
    val id: String,
    val title: String,
    val description: String,
    val target: Int,
    val current: Int,
    val unit: String,
    val category: GoalCategory,
    val reward: GoalReward,
    val isCompleted: Boolean = false
)

data class UpcomingSession(
    val id: String,
    val title: String,
    val type: String,
    val scheduledTime: LocalDateTime,
    val duration: Int,
    val difficulty: String,
    val isOptimal: Boolean = false
)

data class RecentAchievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val xpReward: Int,
    val timestamp: LocalDateTime,
    val rarity: AchievementRarity
)

data class WeatherInfo(
    val temperature: Int,
    val condition: String,
    val emoji: String,
    val suggestion: String
)

data class WidgetData(
    val value: Any,
    val metadata: Map<String, Any> = emptyMap()
)

data class GoalReward(
    val xp: Int,
    val coins: Int = 0,
    val badge: String? = null
)

enum class WidgetType {
    QUICK_STATS,
    PERFORMANCE_CHART,
    DAILY_GOALS,
    RECENT_ACTIVITY,
    UPCOMING_SESSIONS,
    RECOMMENDATIONS,
    ACHIEVEMENTS,
    LEADERBOARD,
    WEATHER,
    STREAK_TRACKER
}

enum class WidgetSize {
    SMALL, MEDIUM, LARGE, EXTRA_LARGE
}

enum class ActivityType {
    DRILL_COMPLETED,
    LEVEL_UP,
    ACHIEVEMENT_UNLOCKED,
    SESSION_COMPLETED,
    GOAL_ACHIEVED,
    STREAK_MILESTONE,
    RANKING_CHANGE
}

enum class RecommendationType {
    SKILL_IMPROVEMENT,
    SCHEDULE_OPTIMIZATION,
    WEAKNESS_FOCUS,
    STRENGTH_BUILDING,
    MENTAL_GAME,
    STUDY_MATERIAL
}

enum class RecommendationPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class GoalCategory {
    PRACTICE, PERFORMANCE, STUDY, SOCIAL, STREAK
}

enum class AchievementRarity {
    COMMON, RARE, EPIC, LEGENDARY
}

enum class MembershipTier {
    FREE, PREMIUM, PRO, MASTER
}

class DashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        startPeriodicUpdates()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // Simulate loading delay
            delay(1000)
            
            val user = generateUserData()
            val widgets = generateWidgets()
            val quickStats = generateQuickStats()
            val recentActivity = generateRecentActivity()
            val recommendations = generateSmartRecommendations()
            val dailyGoals = generateDailyGoals()
            val upcomingSessions = generateUpcomingSessions()
            val achievements = generateRecentAchievements()
            val weather = generateWeatherInfo()
            
            _uiState.value = _uiState.value.copy(
                user = user,
                widgets = widgets,
                quickStats = quickStats,
                recentActivity = recentActivity,
                recommendations = recommendations,
                dailyGoals = dailyGoals,
                upcomingSessions = upcomingSessions,
                achievements = achievements,
                weatherInfo = weather,
                isLoading = false,
                lastUpdated = LocalDateTime.now()
            )
        }
    }

    fun refreshDashboard() {
        loadDashboardData()
    }

    fun toggleWidget(widgetId: String) {
        viewModelScope.launch {
            val updatedWidgets = _uiState.value.widgets.map { widget ->
                if (widget.id == widgetId) {
                    widget.copy(isVisible = !widget.isVisible)
                } else widget
            }
            
            _uiState.value = _uiState.value.copy(widgets = updatedWidgets)
        }
    }

    fun reorderWidgets(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            val widgets = _uiState.value.widgets.toMutableList()
            val item = widgets.removeAt(fromIndex)
            widgets.add(toIndex, item)
            
            val reorderedWidgets = widgets.mapIndexed { index, widget ->
                widget.copy(position = index)
            }
            
            _uiState.value = _uiState.value.copy(widgets = reorderedWidgets)
        }
    }

    fun completeGoal(goalId: String) {
        viewModelScope.launch {
            val updatedGoals = _uiState.value.dailyGoals.map { goal ->
                if (goal.id == goalId) {
                    goal.copy(isCompleted = true, current = goal.target)
                } else goal
            }
            
            _uiState.value = _uiState.value.copy(dailyGoals = updatedGoals)
            
            // Add achievement for completing goal
            addActivityItem(
                ActivityItem(
                    id = "goal_completed_${System.currentTimeMillis()}",
                    type = ActivityType.GOAL_ACHIEVED,
                    title = "Meta Concluída!",
                    description = "Você completou uma meta diária",
                    timestamp = LocalDateTime.now(),
                    icon = "🎯"
                )
            )
        }
    }

    private fun addActivityItem(activity: ActivityItem) {
        val updatedActivity = listOf(activity) + _uiState.value.recentActivity.take(9)
        _uiState.value = _uiState.value.copy(recentActivity = updatedActivity)
    }

    private fun startPeriodicUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(300000) // Update every 5 minutes
                
                // Update quick stats
                val updatedStats = _uiState.value.quickStats.copy(
                    todayHands = _uiState.value.quickStats.todayHands + (1..3).random(),
                    todayXP = _uiState.value.quickStats.todayXP + (10..50).random()
                )
                
                _uiState.value = _uiState.value.copy(
                    quickStats = updatedStats,
                    lastUpdated = LocalDateTime.now()
                )
            }
        }
    }

    private fun generateUserData(): DashboardUser {
        return DashboardUser(
            name = "Alex",
            level = 13,
            currentXP = 2450,
            nextLevelXP = 3000,
            bankroll = 12580.50,
            winRate = 67.3,
            currentStreak = 7,
            totalHands = 1247,
            rankPosition = 156,
            avatar = "🎯",
            membership = MembershipTier.PREMIUM
        )
    }

    private fun generateWidgets(): List<DashboardWidget> {
        return listOf(
            DashboardWidget(
                id = "quick_stats",
                type = WidgetType.QUICK_STATS,
                title = "Estatísticas Rápidas",
                data = WidgetData(generateQuickStats()),
                position = 0,
                size = WidgetSize.LARGE
            ),
            DashboardWidget(
                id = "daily_goals",
                type = WidgetType.DAILY_GOALS,
                title = "Metas Diárias",
                data = WidgetData(generateDailyGoals()),
                position = 1,
                size = WidgetSize.MEDIUM
            ),
            DashboardWidget(
                id = "recommendations",
                type = WidgetType.RECOMMENDATIONS,
                title = "Recomendações IA",
                data = WidgetData(generateSmartRecommendations()),
                position = 2,
                size = WidgetSize.MEDIUM
            ),
            DashboardWidget(
                id = "recent_activity",
                type = WidgetType.RECENT_ACTIVITY,
                title = "Atividade Recente",
                data = WidgetData(generateRecentActivity()),
                position = 3,
                size = WidgetSize.MEDIUM
            )
        )
    }

    private fun generateQuickStats(): QuickStats {
        return QuickStats(
            todayHands = 23,
            todayXP = 340,
            weeklyAccuracy = 89.5,
            currentPosition = 156,
            improvementRate = 12.3,
            studyTime = 2.5,
            completedChallenges = 3,
            activeChallenges = 2
        )
    }

    private fun generateRecentActivity(): List<ActivityItem> {
        return listOf(
            ActivityItem(
                id = "1",
                type = ActivityType.ACHIEVEMENT_UNLOCKED,
                title = "Nova Conquista!",
                description = "Mestre da Precisão desbloqueada",
                timestamp = LocalDateTime.now().minusMinutes(15),
                value = "+500 XP",
                icon = "🏆"
            ),
            ActivityItem(
                id = "2",
                type = ActivityType.DRILL_COMPLETED,
                title = "Drill Concluído",
                description = "Pré-flop Fundamentals - 95% precisão",
                timestamp = LocalDateTime.now().minusHours(1),
                value = "+150 XP",
                icon = "🎯"
            ),
            ActivityItem(
                id = "3",
                type = ActivityType.STREAK_MILESTONE,
                title = "Sequência de 7 dias!",
                description = "Você está em fogo! Continue assim",
                timestamp = LocalDateTime.now().minusHours(2),
                value = "+200 XP",
                icon = "🔥"
            ),
            ActivityItem(
                id = "4",
                type = ActivityType.RANKING_CHANGE,
                title = "Subiu no Ranking",
                description = "Posição 156 → 145 no ranking global",
                timestamp = LocalDateTime.now().minusHours(3),
                icon = "📈"
            ),
            ActivityItem(
                id = "5",
                type = ActivityType.SESSION_COMPLETED,
                title = "Sessão de Estudo",
                description = "Análise Pós-flop - 2h30min",
                timestamp = LocalDateTime.now().minusHours(4),
                value = "+300 XP",
                icon = "📚"
            )
        )
    }

    private fun generateSmartRecommendations(): List<SmartRecommendation> {
        return listOf(
            SmartRecommendation(
                id = "1",
                type = RecommendationType.WEAKNESS_FOCUS,
                title = "Trabalhe Situações 3-bet",
                description = "Sua precisão em situações de 3-bet está abaixo da média. Um drill focado pode melhorar significativamente seu desempenho.",
                priority = RecommendationPriority.HIGH,
                estimatedTime = 15,
                potentialImprovement = 18.5,
                confidence = 0.87,
                actionText = "Praticar Agora"
            ),
            SmartRecommendation(
                id = "2",
                type = RecommendationType.SCHEDULE_OPTIMIZATION,
                title = "Horário Ideal Detectado",
                description = "Baseado em seus dados, você tem melhor performance entre 19h-21h. Agende uma sessão!",
                priority = RecommendationPriority.MEDIUM,
                estimatedTime = 45,
                potentialImprovement = 12.3,
                confidence = 0.92,
                actionText = "Agendar Sessão"
            ),
            SmartRecommendation(
                id = "3",
                type = RecommendationType.MENTAL_GAME,
                title = "Exercício de Mindfulness",
                description = "Seus dados mostram tensão em situações de alta pressão. Técnicas de respiração podem ajudar.",
                priority = RecommendationPriority.MEDIUM,
                estimatedTime = 10,
                potentialImprovement = 8.7,
                confidence = 0.74,
                actionText = "Iniciar Exercício"
            )
        )
    }

    private fun generateDailyGoals(): List<DailyGoal> {
        return listOf(
            DailyGoal(
                id = "1",
                title = "Completar 5 Drills",
                description = "Pratique diferentes cenários",
                target = 5,
                current = 3,
                unit = "drills",
                category = GoalCategory.PRACTICE,
                reward = GoalReward(xp = 200, coins = 50),
                isCompleted = false
            ),
            DailyGoal(
                id = "2",
                title = "90% de Precisão",
                description = "Mantenha alta precisão nos drills",
                target = 90,
                current = 87,
                unit = "%",
                category = GoalCategory.PERFORMANCE,
                reward = GoalReward(xp = 300, coins = 75),
                isCompleted = false
            ),
            DailyGoal(
                id = "3",
                title = "30 min de Estudo",
                description = "Estude conceitos teóricos",
                target = 30,
                current = 30,
                unit = "min",
                category = GoalCategory.STUDY,
                reward = GoalReward(xp = 150, coins = 25),
                isCompleted = true
            )
        )
    }

    private fun generateUpcomingSessions(): List<UpcomingSession> {
        return listOf(
            UpcomingSession(
                id = "1",
                title = "Análise Pós-flop",
                type = "Conceitual",
                scheduledTime = LocalDateTime.now().plusHours(2),
                duration = 45,
                difficulty = "Intermediário",
                isOptimal = true
            ),
            UpcomingSession(
                id = "2",
                title = "Drill: Ranges de 4-bet",
                type = "Prática",
                scheduledTime = LocalDateTime.now().plusHours(5),
                duration = 20,
                difficulty = "Avançado",
                isOptimal = false
            )
        )
    }

    private fun generateRecentAchievements(): List<RecentAchievement> {
        return listOf(
            RecentAchievement(
                id = "1",
                title = "Mestre da Precisão",
                description = "Alcançou 95% de precisão em 10 drills consecutivos",
                icon = "🎯",
                xpReward = 500,
                timestamp = LocalDateTime.now().minusMinutes(15),
                rarity = AchievementRarity.EPIC
            ),
            RecentAchievement(
                id = "2",
                title = "Sequência de Ferro",
                description = "7 dias consecutivos de treino",
                icon = "🔥",
                xpReward = 300,
                timestamp = LocalDateTime.now().minusHours(2),
                rarity = AchievementRarity.RARE
            )
        )
    }

    private fun generateWeatherInfo(): WeatherInfo {
        return WeatherInfo(
            temperature = 24,
            condition = "Ensolarado",
            emoji = "☀️",
            suggestion = "Ótimo dia para uma sessão de estudo ao ar livre!"
        )
    }
}
