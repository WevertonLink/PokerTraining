package com.pokertrainer.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex
import com.pokertrainer.ui.viewmodels.*

@Composable
fun GamificationScreen(
    viewModel: GamificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableIntStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        TopGreetingBar("Alex")
        
        // Player Level & XP Card
        PlayerLevelCard(
            level = uiState.playerLevel,
            currentXP = uiState.currentXP,
            xpToNextLevel = uiState.xpToNextLevel,
            totalXP = uiState.totalXP
        )
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        
        // Season Rank & Progress
        SeasonRankCard(
            rank = uiState.seasonRank,
            progress = uiState.rankProgress
        )
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        
        // Weekly Streak Progress
        WeeklyStreakCard(
            weeklyProgress = uiState.weeklyProgress,
            currentStreak = uiState.currentStreak,
            bestStreak = uiState.bestStreak
        )
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        
        // Tab Navigation
        GamificationTabs(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        
        Spacer(Modifier.height(16.dp))
        
        // Tab Content
        when (selectedTab) {
            0 -> {
                // Daily Challenges
                SectionHeader("Desafios Diários")
                DailyChallengesSection(
                    challenges = uiState.dailyChallenges,
                    onCompleteChallenge = viewModel::completeChallenge,
                    onUpdateProgress = viewModel::updateChallengeProgress
                )
            }
            1 -> {
                // Achievements
                SectionHeader("Conquistas")
                AchievementsSection(
                    achievements = uiState.achievements,
                    onClaimAchievement = viewModel::claimAchievement
                )
            }
            2 -> {
                // Badges Collection
                SectionHeader("Coleção de Badges")
                BadgesSection(badges = uiState.badges)
            }
            3 -> {
                // Leaderboard
                SectionHeader("Ranking Global")
                LeaderboardSection(leaderboard = uiState.leaderboard)
            }
        }
        
        // Bottom padding for bottom bar
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun PlayerLevelCard(
    level: Int,
    currentXP: Int,
    xpToNextLevel: Int,
    totalXP: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Level Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    colorFromHex(Tokens.Primary),
                                    colorFromHex(Tokens.Primary).copy(alpha = 0.7f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LVL",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = level.toString(),
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // XP Information
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "XP: $currentXP",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Meta: $xpToNextLevel",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorFromHex(Tokens.Neutral)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // XP Progress Bar
                    LinearProgressIndicator(
                        progress = { currentXP.toFloat() / xpToNextLevel.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = colorFromHex(Tokens.Primary),
                        trackColor = colorFromHex(Tokens.Surface)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "XP Total: ${totalXP.toString().reversed().chunked(3).joinToString(".").reversed()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Next Level Rewards Preview
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorFromHex(Tokens.Primary).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Próximas recompensas",
                        tint = colorFromHex(Tokens.Primary),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Próximo nível: Novo badge + 500 XP bônus",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorFromHex(Tokens.Primary),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SeasonRankCard(
    rank: SeasonRank,
    progress: Float
) {
    val rankColor = when (rank) {
        SeasonRank.BRONZE -> "#CD7F32"
        SeasonRank.SILVER -> "#C0C0C0"
        SeasonRank.GOLD -> "#FFD700"
        SeasonRank.PLATINUM -> "#E5E4E2"
        SeasonRank.DIAMOND -> "#B9F2FF"
        SeasonRank.MASTER -> "#FF00FF"
    }
    
    val rankEmoji = when (rank) {
        SeasonRank.BRONZE -> "🥉"
        SeasonRank.SILVER -> "🥈"
        SeasonRank.GOLD -> "🥇"
        SeasonRank.PLATINUM -> "💎"
        SeasonRank.DIAMOND -> "💠"
        SeasonRank.MASTER -> "👑"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = colorFromHex(rankColor).copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rankEmoji,
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rank da Temporada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorFromHex(Tokens.Neutral)
                )
                
                Text(
                    text = rank.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorFromHex(rankColor),
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = colorFromHex(rankColor),
                    trackColor = colorFromHex(Tokens.Surface)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${(progress * 100).toInt()}% para o próximo rank",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
        }
    }
}

@Composable
private fun WeeklyStreakCard(
    weeklyProgress: WeeklyProgress,
    currentStreak: Int,
    bestStreak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
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
                Column {
                    Text(
                        text = "Sequência Atual",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = colorFromHex(Tokens.Warning),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$currentStreak dias",
                            style = MaterialTheme.typography.titleLarge,
                            color = colorFromHex(Tokens.Warning),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Melhor Sequência",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    Text(
                        text = "$bestStreak dias",
                        style = MaterialTheme.typography.titleMedium,
                        color = colorFromHex(Tokens.Positive),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weekly Progress Grid
            Text(
                text = "Progresso da Semana",
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("S", "T", "Q", "Q", "S", "S", "D")
                val completed = listOf(
                    weeklyProgress.mon, weeklyProgress.tue, weeklyProgress.wed,
                    weeklyProgress.thu, weeklyProgress.fri, weeklyProgress.sat, weeklyProgress.sun
                )
                
                days.forEachIndexed { index, day ->
                    WeeklyDayChip(
                        day = day,
                        isCompleted = completed[index],
                        isToday = index == 3 // Thursday for example
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { weeklyProgress.completedDays.toFloat() / weeklyProgress.targetDays.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = colorFromHex(Tokens.Positive),
                trackColor = colorFromHex(Tokens.Surface)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${weeklyProgress.completedDays}/${weeklyProgress.targetDays} dias completados",
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
private fun WeeklyDayChip(
    day: String,
    isCompleted: Boolean,
    isToday: Boolean
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(
                color = when {
                    isCompleted -> colorFromHex(Tokens.Positive)
                    isToday -> colorFromHex(Tokens.Primary)
                    else -> colorFromHex(Tokens.Surface)
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Completo",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Text(
                text = day,
                fontSize = 12.sp,
                color = if (isToday) Color.White else colorFromHex(Tokens.Neutral),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GamificationTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("Desafios", "Conquistas", "Badges", "Ranking")
    
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = Color.White,
        indicator = { tabPositions ->
            if (selectedTab < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    color = colorFromHex(Tokens.Primary)
                )
            }
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                text = {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                    )
                },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                selectedContentColor = colorFromHex(Tokens.Primary),
            )
        }
    }
}

@Composable
private fun DailyChallengesSection(
    challenges: List<DailyChallenge>,
    onCompleteChallenge: (String) -> Unit,
    onUpdateProgress: (String, Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        challenges.forEach { challenge ->
            DailyChallengeCard(
                challenge = challenge,
                onComplete = { onCompleteChallenge(challenge.id) },
                onUpdateProgress = { progress -> onUpdateProgress(challenge.id, progress) }
            )
        }
    }
}

@Composable
private fun DailyChallengeCard(
    challenge: DailyChallenge,
    onComplete: () -> Unit,
    onUpdateProgress: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (challenge.isCompleted) {
                colorFromHex(Tokens.Positive).copy(alpha = 0.1f)
            } else {
                colorFromHex(Tokens.SurfaceElevated)
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (challenge.challengeType) {
                                ChallengeType.ACCURACY -> Icons.Default.GpsFixed
                                ChallengeType.SPEED -> Icons.Default.Speed
                                ChallengeType.VOLUME -> Icons.Default.Repeat
                                ChallengeType.STREAK -> Icons.Default.LocalFireDepartment
                                ChallengeType.LEARNING -> Icons.Default.School
                            },
                            contentDescription = challenge.challengeType.name,
                            tint = if (challenge.isCompleted) {
                                colorFromHex(Tokens.Positive)
                            } else {
                                colorFromHex(Tokens.Primary)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = challenge.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (challenge.isCompleted) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completo",
                                tint = colorFromHex(Tokens.Positive),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { challenge.currentProgress.toFloat() / challenge.targetValue.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (challenge.isCompleted) {
                            colorFromHex(Tokens.Positive)
                        } else {
                            colorFromHex(Tokens.Primary)
                        },
                        trackColor = colorFromHex(Tokens.Surface)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${challenge.currentProgress}/${challenge.targetValue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorFromHex(Tokens.Neutral)
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "XP",
                                tint = colorFromHex(Tokens.Warning),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${challenge.xpReward} XP",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorFromHex(Tokens.Warning),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            
            if (challenge.currentProgress >= challenge.targetValue && !challenge.isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorFromHex(Tokens.Positive)
                    )
                ) {
                    Text(
                        text = "Reivindicar Recompensa",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementsSection(
    achievements: List<Achievement>,
    onClaimAchievement: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        achievements.forEach { achievement ->
            AchievementCard(
                achievement = achievement,
                onClaim = { onClaimAchievement(achievement.id) }
            )
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    onClaim: () -> Unit
) {
    val rarityColor = when (achievement.rarity) {
        AchievementRarity.COMMON -> "#CCCCCC"
        AchievementRarity.RARE -> "#3498DB"
        AchievementRarity.EPIC -> "#9B59B6"
        AchievementRarity.LEGENDARY -> "#F39C12"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isCompleted) {
                colorFromHex(rarityColor).copy(alpha = 0.1f)
            } else {
                colorFromHex(Tokens.SurfaceElevated)
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Achievement Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = colorFromHex(rarityColor).copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.icon,
                        fontSize = 28.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = achievement.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Rarity Badge
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = colorFromHex(rarityColor)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = achievement.rarity.name,
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (!achievement.isCompleted) {
                        LinearProgressIndicator(
                            progress = { achievement.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = colorFromHex(rarityColor),
                            trackColor = colorFromHex(Tokens.Surface)
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "${(achievement.progress * achievement.maxProgress).toInt()}/${achievement.maxProgress}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorFromHex(Tokens.Neutral)
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = "XP",
                            tint = colorFromHex(Tokens.Warning),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${achievement.xpReward} XP",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorFromHex(Tokens.Warning),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                if (achievement.isCompleted && achievement.unlockedAt != null) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Desbloqueado",
                        tint = colorFromHex(Tokens.Positive),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            if (achievement.progress >= 1f && !achievement.isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onClaim,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorFromHex(rarityColor)
                    )
                ) {
                    Text(
                        text = "Reivindicar ${achievement.xpReward} XP",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgesSection(
    badges: List<Badge>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(badges) { badge ->
            BadgeCard(badge = badge)
        }
    }
}

@Composable
private fun BadgeCard(
    badge: Badge
) {
    Card(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = colorFromHex(badge.color).copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.icon,
                    fontSize = 28.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = badge.name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = badge.description,
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Neutral),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colorFromHex(badge.color)
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = badge.category.name,
                    fontSize = 10.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LeaderboardSection(
    leaderboard: List<LeaderboardEntry>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        leaderboard.forEach { entry ->
            LeaderboardCard(entry = entry)
        }
    }
}

@Composable
private fun LeaderboardCard(
    entry: LeaderboardEntry
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (entry.isCurrentPlayer) {
                colorFromHex(Tokens.Primary).copy(alpha = 0.1f)
            } else {
                colorFromHex(Tokens.SurfaceElevated)
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = when (entry.rank) {
                            1 -> colorFromHex("#FFD700")
                            2 -> colorFromHex("#C0C0C0")
                            3 -> colorFromHex("#CD7F32")
                            else -> colorFromHex(Tokens.Primary)
                        }.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.rank.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = when (entry.rank) {
                        1 -> colorFromHex("#FFD700")
                        2 -> colorFromHex("#C0C0C0")
                        3 -> colorFromHex("#CD7F32")
                        else -> colorFromHex(Tokens.Primary)
                    },
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Avatar
            Text(
                text = entry.avatar,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Player Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.playerName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (entry.isCurrentPlayer) {
                        colorFromHex(Tokens.Primary)
                    } else {
                        Color.White
                    },
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Nível ${entry.level}",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "• ${entry.xp.toString().reversed().chunked(3).joinToString(".").reversed()} XP",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
            }
            
            // Weekly XP
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "+${entry.weeklyXP}",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorFromHex(Tokens.Positive),
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "esta semana",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
        }
    }
}
