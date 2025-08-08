package com.pokertrainer.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.ui.theme.*

@Composable
fun DashboardQuickAction(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    color: Color = colorFromHex(Tokens.Primary),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = color.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorFromHex(Tokens.Neutral),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Ir para",
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DashboardMetricCard(
    label: String,
    value: String,
    change: String? = null,
    isPositive: Boolean = true,
    icon: String = "📊",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = colorFromHex(Tokens.Neutral),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            change?.let {
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = if (isPositive) "Aumento" else "Diminuição",
                        tint = if (isPositive) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Error),
                        modifier = Modifier.size(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(2.dp))
                    
                    Text(
                        text = change,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPositive) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Error),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardProgressRing(
    progress: Float,
    label: String,
    value: String,
    maxValue: String,
    color: Color = colorFromHex(Tokens.Primary),
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "Progress Animation"
    )
    
    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = color.copy(alpha = 0.2f),
            strokeWidth = 8.dp
        )
        
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = 8.dp
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "de $maxValue",
                style = MaterialTheme.typography.labelSmall,
                color = colorFromHex(Tokens.Neutral)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
fun DashboardStreakIndicator(
    currentStreak: Int,
    bestStreak: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.Warning).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥",
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sequência Atual",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorFromHex(Tokens.Neutral)
                )
                Text(
                    text = "$currentStreak dias",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorFromHex(Tokens.Warning),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Recorde: $bestStreak dias",
                    style = MaterialTheme.typography.labelSmall,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
            
            if (currentStreak >= bestStreak) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorFromHex(Tokens.Warning)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "NOVO RECORDE!",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardLevelProgress(
    currentLevel: Int,
    currentXP: Int,
    nextLevelXP: Int,
    modifier: Modifier = Modifier
) {
    val progress = currentXP.toFloat() / nextLevelXP.toFloat()
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
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
                    text = "Level $currentLevel",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "$currentXP / $nextLevelXP XP",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = colorFromHex(Tokens.Primary),
                trackColor = colorFromHex(Tokens.Neutral).copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${(progress * 100).toInt()}% para o próximo level",
                style = MaterialTheme.typography.labelSmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
fun DashboardQuickStats(
    stats: List<DashboardStat>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📊 Estatísticas Rápidas",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(stats) { stat ->
                    DashboardStatItem(stat = stat)
                }
            }
        }
    }
}

@Composable
private fun DashboardStatItem(
    stat: DashboardStat
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Text(
            text = stat.icon,
            fontSize = 20.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = stat.value,
            style = MaterialTheme.typography.titleMedium,
            color = stat.color,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = stat.label,
            style = MaterialTheme.typography.labelSmall,
            color = colorFromHex(Tokens.Neutral),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DashboardRecentAchievement(
    achievement: DashboardAchievement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = achievement.color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = achievement.icon,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorFromHex(Tokens.Neutral),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "+${achievement.xp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = achievement.color,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (achievement.isNew) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = colorFromHex(Tokens.Error),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun DashboardWeatherWidget(
    temperature: Int,
    condition: String,
    emoji: String,
    suggestion: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.Primary).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$temperature°C • $condition",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = suggestion,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorFromHex(Tokens.Neutral),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Data classes for dashboard components
data class DashboardStat(
    val label: String,
    val value: String,
    val icon: String,
    val color: Color
)

data class DashboardAchievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val xp: Int,
    val color: Color,
    val isNew: Boolean = false
)
