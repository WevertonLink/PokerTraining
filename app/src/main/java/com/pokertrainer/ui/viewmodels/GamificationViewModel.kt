package com.pokertrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.random.Random

data class GamificationUiState(
    val playerLevel: Int = 12,
    val currentXP: Int = 2850,
    val xpToNextLevel: Int = 3200,
    val totalXP: Int = 15420,
    val currentStreak: Int = 7,
    val bestStreak: Int = 23,
    val weeklyProgress: WeeklyProgress = WeeklyProgress(),
    val achievements: List<Achievement> = emptyList(),
    val badges: List<Badge> = emptyList(),
    val dailyChallenges: List<DailyChallenge> = emptyList(),
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val seasonRank: SeasonRank = SeasonRank.GOLD,
    val rankProgress: Float = 0.65f,
    val isLoading: Boolean = false
)

data class WeeklyProgress(
    val mon: Boolean = true,
    val tue: Boolean = true,
    val wed: Boolean = false,
    val thu: Boolean = true,
    val fri: Boolean = false,
    val sat: Boolean = false,
    val sun: Boolean = false,
    val completedDays: Int = 3,
    val targetDays: Int = 5
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val xpReward: Int,
    val isCompleted: Boolean,
    val progress: Float,
    val maxProgress: Int,
    val category: AchievementCategory,
    val rarity: AchievementRarity,
    val unlockedAt: LocalDateTime?
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val color: String,
    val earnedAt: LocalDateTime,
    val category: BadgeCategory
)

data class DailyChallenge(
    val id: String,
    val title: String,
    val description: String,
    val targetValue: Int,
    val currentProgress: Int,
    val xpReward: Int,
    val timeLimit: LocalDateTime,
    val isCompleted: Boolean,
    val challengeType: ChallengeType
)

data class LeaderboardEntry(
    val rank: Int,
    val playerName: String,
    val avatar: String,
    val level: Int,
    val xp: Int,
    val weeklyXP: Int,
    val isCurrentPlayer: Boolean = false
)

enum class AchievementCategory {
    TRAINING, ACCURACY, STREAK, SOCIAL, SPECIAL
}

enum class AchievementRarity {
    COMMON, RARE, EPIC, LEGENDARY
}

enum class BadgeCategory {
    SKILL, MILESTONE, TOURNAMENT, SPECIAL
}

enum class ChallengeType {
    ACCURACY, SPEED, VOLUME, STREAK, LEARNING
}

enum class SeasonRank {
    BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER
}

class GamificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GamificationUiState())
    val uiState: StateFlow<GamificationUiState> = _uiState.asStateFlow()

    init {
        loadGamificationData()
    }

    private fun loadGamificationData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                achievements = generateSampleAchievements(),
                badges = generateSampleBadges(),
                dailyChallenges = generateDailyChallenges(),
                leaderboard = generateLeaderboard()
            )
        }
    }

    fun completeChallenge(challengeId: String) {
        viewModelScope.launch {
            val updatedChallenges = _uiState.value.dailyChallenges.map { challenge ->
                if (challenge.id == challengeId && !challenge.isCompleted) {
                    // Award XP
                    val newXP = _uiState.value.currentXP + challenge.xpReward
                    val newLevel = calculateLevel(newXP)
                    
                    _uiState.value = _uiState.value.copy(
                        currentXP = newXP,
                        playerLevel = newLevel,
                        totalXP = _uiState.value.totalXP + challenge.xpReward
                    )
                    
                    challenge.copy(
                        isCompleted = true,
                        currentProgress = challenge.targetValue
                    )
                } else challenge
            }
            
            _uiState.value = _uiState.value.copy(dailyChallenges = updatedChallenges)
        }
    }

    fun claimAchievement(achievementId: String) {
        viewModelScope.launch {
            val updatedAchievements = _uiState.value.achievements.map { achievement ->
                if (achievement.id == achievementId && !achievement.isCompleted) {
                    val newXP = _uiState.value.currentXP + achievement.xpReward
                    val newLevel = calculateLevel(newXP)
                    
                    _uiState.value = _uiState.value.copy(
                        currentXP = newXP,
                        playerLevel = newLevel,
                        totalXP = _uiState.value.totalXP + achievement.xpReward
                    )
                    
                    achievement.copy(
                        isCompleted = true,
                        unlockedAt = LocalDateTime.now()
                    )
                } else achievement
            }
            
            _uiState.value = _uiState.value.copy(achievements = updatedAchievements)
        }
    }

    fun updateChallengeProgress(challengeId: String, progress: Int) {
        viewModelScope.launch {
            val updatedChallenges = _uiState.value.dailyChallenges.map { challenge ->
                if (challenge.id == challengeId) {
                    val newProgress = (challenge.currentProgress + progress).coerceAtMost(challenge.targetValue)
                    val isCompleted = newProgress >= challenge.targetValue
                    
                    if (isCompleted && !challenge.isCompleted) {
                        // Auto-complete challenge
                        completeChallenge(challengeId)
                    }
                    
                    challenge.copy(currentProgress = newProgress)
                } else challenge
            }
            
            _uiState.value = _uiState.value.copy(dailyChallenges = updatedChallenges)
        }
    }

    private fun calculateLevel(xp: Int): Int {
        return (xp / 1000) + 1
    }

    private fun generateSampleAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "first_win",
                title = "Primeira Vitória",
                description = "Complete sua primeira sessão de treino",
                icon = "🏆",
                xpReward = 100,
                isCompleted = true,
                progress = 1f,
                maxProgress = 1,
                category = AchievementCategory.TRAINING,
                rarity = AchievementRarity.COMMON,
                unlockedAt = LocalDateTime.now().minusDays(5)
            ),
            Achievement(
                id = "accuracy_master",
                title = "Mestre da Precisão",
                description = "Alcance 90% de precisão em 10 sessões",
                icon = "🎯",
                xpReward = 500,
                isCompleted = false,
                progress = 0.7f,
                maxProgress = 10,
                category = AchievementCategory.ACCURACY,
                rarity = AchievementRarity.EPIC,
                unlockedAt = null
            ),
            Achievement(
                id = "streak_legend",
                title = "Lenda das Sequências",
                description = "Mantenha uma sequência de 30 dias",
                icon = "🔥",
                xpReward = 1000,
                isCompleted = false,
                progress = 0.23f,
                maxProgress = 30,
                category = AchievementCategory.STREAK,
                rarity = AchievementRarity.LEGENDARY,
                unlockedAt = null
            ),
            Achievement(
                id = "speed_demon",
                title = "Demônio da Velocidade",
                description = "Complete 50 drills em menos de 3 segundos cada",
                icon = "⚡",
                xpReward = 300,
                isCompleted = false,
                progress = 0.6f,
                maxProgress = 50,
                category = AchievementCategory.TRAINING,
                rarity = AchievementRarity.RARE,
                unlockedAt = null
            ),
            Achievement(
                id = "social_butterfly",
                title = "Borboleta Social",
                description = "Participe de 5 grupos de estudo",
                icon = "👥",
                xpReward = 250,
                isCompleted = true,
                progress = 1f,
                maxProgress = 5,
                category = AchievementCategory.SOCIAL,
                rarity = AchievementRarity.RARE,
                unlockedAt = LocalDateTime.now().minusDays(2)
            )
        )
    }

    private fun generateSampleBadges(): List<Badge> {
        return listOf(
            Badge(
                id = "week_warrior",
                name = "Guerreiro Semanal",
                description = "Completou todas as metas da semana",
                icon = "⚔️",
                color = "#FFD700",
                earnedAt = LocalDateTime.now().minusDays(1),
                category = BadgeCategory.MILESTONE
            ),
            Badge(
                id = "accuracy_ace",
                name = "Ás da Precisão",
                description = "95% de precisão em sessão única",
                icon = "🎯",
                color = "#FF6B6B",
                earnedAt = LocalDateTime.now().minusDays(3),
                category = BadgeCategory.SKILL
            ),
            Badge(
                id = "tournament_champion",
                name = "Campeão de Torneio",
                description = "Venceu um torneio comunitário",
                icon = "👑",
                color = "#4ECDC4",
                earnedAt = LocalDateTime.now().minusDays(7),
                category = BadgeCategory.TOURNAMENT
            )
        )
    }

    private fun generateDailyChallenges(): List<DailyChallenge> {
        return listOf(
            DailyChallenge(
                id = "daily_accuracy",
                title = "Precisão Diária",
                description = "Alcance 85% de precisão em qualquer sessão",
                targetValue = 85,
                currentProgress = 78,
                xpReward = 150,
                timeLimit = LocalDateTime.now().plusHours(18),
                isCompleted = false,
                challengeType = ChallengeType.ACCURACY
            ),
            DailyChallenge(
                id = "daily_speed",
                title = "Velocidade Relâmpago",
                description = "Complete 20 drills em menos de 5 segundos cada",
                targetValue = 20,
                currentProgress = 12,
                xpReward = 120,
                timeLimit = LocalDateTime.now().plusHours(18),
                isCompleted = false,
                challengeType = ChallengeType.SPEED
            ),
            DailyChallenge(
                id = "daily_volume",
                title = "Volume de Treino",
                description = "Complete 3 sessões de treino diferentes",
                targetValue = 3,
                currentProgress = 1,
                xpReward = 200,
                timeLimit = LocalDateTime.now().plusHours(18),
                isCompleted = false,
                challengeType = ChallengeType.VOLUME
            )
        )
    }

    private fun generateLeaderboard(): List<LeaderboardEntry> {
        return listOf(
            LeaderboardEntry(1, "PokerPro2024", "🥇", 25, 24850, 1250, false),
            LeaderboardEntry(2, "CardShark", "🦈", 23, 22450, 1180, false),
            LeaderboardEntry(3, "BluffMaster", "🎭", 21, 20120, 1050, false),
            LeaderboardEntry(4, "Alex", "🎯", 12, 15420, 890, true), // Current player
            LeaderboardEntry(5, "RiverRat", "🐭", 18, 17890, 820, false),
            LeaderboardEntry(6, "AllInAnnie", "♠️", 16, 15650, 780, false),
            LeaderboardEntry(7, "FoldFriend", "🃏", 14, 13240, 720, false),
            LeaderboardEntry(8, "CheckChamp", "✅", 13, 12890, 680, false)
        )
    }
}
