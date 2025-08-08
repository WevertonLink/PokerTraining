package com.pokertrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokertrainer.domain.models.*
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex
import com.pokertrainer.ui.viewmodels.AnalysisTab
import com.pokertrainer.ui.viewmodels.PerformanceAnalysisViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceAnalysisScreen(
    viewModel: PerformanceAnalysisViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient())
    ) {
        // Top Bar
        TopAppBar(
            title = { 
                Text(
                    "Análise de Performance", 
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall
                ) 
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorFromHex(Tokens.Primary))
            }
        } else if (uiState.error != null) {
            ErrorCard(
                error = uiState.error!!,
                onDismiss = { viewModel.dismissError() }
            )
        } else {
            uiState.analysis?.let { analysis ->
                PerformanceContent(
                    analysis = analysis,
                    selectedPeriod = uiState.selectedPeriod,
                    selectedTab = uiState.selectedTab,
                    expandedCategories = uiState.expandedCategories,
                    onPeriodChange = { viewModel.changePeriod(it) },
                    onTabSelect = { viewModel.selectTab(it) },
                    onCategoryToggle = { viewModel.toggleCategoryExpansion(it) },
                    onRecommendationViewed = { viewModel.markRecommendationAsViewed(it) }
                )
            }
        }
    }
}

@Composable
private fun PerformanceContent(
    analysis: PerformanceAnalysis,
    selectedPeriod: AnalysisPeriod,
    selectedTab: AnalysisTab,
    expandedCategories: Set<DrillCategory>,
    onPeriodChange: (AnalysisPeriod) -> Unit,
    onTabSelect: (AnalysisTab) -> Unit,
    onCategoryToggle: (DrillCategory) -> Unit,
    onRecommendationViewed: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        // Period Selector
        PeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodChange = onPeriodChange
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Tab Selector
        TabSelector(
            selectedTab = selectedTab,
            onTabSelect = onTabSelect
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content based on selected tab
        when (selectedTab) {
            AnalysisTab.OVERVIEW -> OverviewTab(analysis)
            AnalysisTab.CATEGORIES -> CategoriesTab(
                analysis.categoryPerformance,
                expandedCategories,
                onCategoryToggle
            )
            AnalysisTab.WEAKNESSES -> WeaknessesTab(analysis.weaknesses)
            AnalysisTab.RECOMMENDATIONS -> RecommendationsTab(
                analysis.recommendations,
                onRecommendationViewed
            )
            AnalysisTab.PROGRESS -> ProgressTab(analysis.progressTrend)
        }
        
        // Bottom padding
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: AnalysisPeriod,
    onPeriodChange: (AnalysisPeriod) -> Unit
) {
    val periods = listOf(
        AnalysisPeriod.LAST_7_DAYS to "7 dias",
        AnalysisPeriod.LAST_30_DAYS to "30 dias",
        AnalysisPeriod.LAST_3_MONTHS to "3 meses",
        AnalysisPeriod.ALL_TIME to "Todos"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        periods.forEach { (period, label) ->
            FilterChip(
                onClick = { onPeriodChange(period) },
                label = { Text(label) },
                selected = selectedPeriod == period,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorFromHex(Tokens.Primary),
                    selectedLabelColor = Color.White,
                    containerColor = colorFromHex(Tokens.SurfaceElevated),
                    labelColor = colorFromHex(Tokens.Neutral)
                )
            )
        }
    }
}

@Composable
private fun TabSelector(
    selectedTab: AnalysisTab,
    onTabSelect: (AnalysisTab) -> Unit
) {
    val tabs = listOf(
        AnalysisTab.OVERVIEW to "Visão Geral",
        AnalysisTab.CATEGORIES to "Categorias",
        AnalysisTab.WEAKNESSES to "Fraquezas",
        AnalysisTab.RECOMMENDATIONS to "Dicas",
        AnalysisTab.PROGRESS to "Progresso"
    )
    
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tabs) { (tab, label) ->
            FilterChip(
                onClick = { onTabSelect(tab) },
                label = { Text(label) },
                selected = selectedTab == tab,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colorFromHex(Tokens.Primary),
                    selectedLabelColor = Color.White,
                    containerColor = colorFromHex(Tokens.SurfaceElevated),
                    labelColor = colorFromHex(Tokens.Neutral)
                )
            )
        }
    }
}

@Composable
private fun OverviewTab(analysis: PerformanceAnalysis) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Overall Stats Card
        StatsSummaryCard(analysis.overallStats)
        
        // Rank Card
        RankCard(analysis.overallStats.rank, analysis.overallStats.averageAccuracy)
        
        // Quick Insights
        QuickInsightsCard(analysis)
    }
}

@Composable
private fun StatsSummaryCard(stats: OverallStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Estatísticas Gerais",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Precisão",
                    value = "${(stats.averageAccuracy * 100).toInt()}%",
                    icon = Icons.AutoMirrored.Filled.TrendingUp
                )
                StatItem(
                    label = "Drills",
                    value = "${stats.totalDrillsCompleted}",
                    icon = Icons.AutoMirrored.Filled.Assignment
                )
                StatItem(
                    label = "Streak",
                    value = "${stats.currentStreak}d",
                    icon = Icons.Default.LocalFireDepartment
                )
            }
            
            LinearProgressIndicator(
                progress = { stats.averageAccuracy.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = colorFromHex(Tokens.Primary),
                trackColor = colorFromHex(Tokens.Neutral).copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colorFromHex(Tokens.Primary),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colorFromHex(Tokens.Neutral)
        )
    }
}

@Composable
private fun RankCard(rank: PlayerRank, accuracy: Double) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Seu Rank",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = rank.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = getRankColor(rank),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(accuracy * 100).toInt()}% de precisão",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
            
            Icon(
                imageVector = getRankIcon(rank),
                contentDescription = rank.name,
                tint = getRankColor(rank),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
private fun QuickInsightsCard(analysis: PerformanceAnalysis) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Insights Rápidos",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Maior fraqueza
            if (analysis.weaknesses.isNotEmpty()) {
                val topWeakness = analysis.weaknesses.maxByOrNull { it.severity.ordinal }
                topWeakness?.let { weakness ->
                    InsightItem(
                        icon = Icons.Default.Warning,
                        title = "Área de Melhoria",
                        description = weakness.specificArea,
                        color = colorFromHex(Tokens.Warning)
                    )
                }
            }
            
            // Melhor categoria
            val bestCategory = analysis.categoryPerformance.maxByOrNull { it.value.accuracy }
            bestCategory?.let { (category, stats) ->
                InsightItem(
                    icon = Icons.Default.Star,
                    title = "Ponto Forte",
                    description = "${category.name} - ${(stats.accuracy * 100).toInt()}%",
                    color = colorFromHex(Tokens.Positive)
                )
            }
        }
    }
}

@Composable
private fun InsightItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
private fun CategoriesTab(
    categoryPerformance: Map<DrillCategory, CategoryStats>,
    expandedCategories: Set<DrillCategory>,
    onCategoryToggle: (DrillCategory) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categoryPerformance.forEach { (category, stats) ->
            CategoryPerformanceCard(
                category = category,
                stats = stats,
                isExpanded = expandedCategories.contains(category),
                onToggle = { onCategoryToggle(category) }
            )
        }
    }
}

@Composable
private fun CategoryPerformanceCard(
    category: DrillCategory,
    stats: CategoryStats,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onToggle
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(stats.accuracy * 100).toInt()}% de precisão",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Recolher" else "Expandir",
                    tint = colorFromHex(Tokens.Primary)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { stats.accuracy.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    stats.accuracy >= 0.8 -> colorFromHex(Tokens.Positive)
                    stats.accuracy >= 0.6 -> colorFromHex(Tokens.Warning)
                    else -> colorFromHex(Tokens.Error)
                },
                trackColor = colorFromHex(Tokens.Neutral).copy(alpha = 0.3f)
            )
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Detalhes expandidos
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailItem("Questões", "${stats.questionsAnswered}")
                    DetailItem("Tempo Médio", "${stats.averageTime / 1000}s")
                    DetailItem("Nível", getMasteryLabel(stats.masteryLevel))
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colorFromHex(Tokens.Neutral)
        )
    }
}

@Composable
private fun WeaknessesTab(weaknesses: List<WeaknessArea>) {
    if (weaknesses.isEmpty()) {
        EmptyStateCard(
            icon = Icons.Default.EmojiEvents,
            title = "Parabéns!",
            description = "Nenhuma fraqueza significativa detectada"
        )
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            weaknesses.forEach { weakness ->
                WeaknessCard(weakness)
            }
        }
    }
}

@Composable
private fun WeaknessCard(weakness: WeaknessArea) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = when (weakness.severity) {
                        WeaknessSeverity.HIGH -> Icons.Default.Error
                        WeaknessSeverity.MEDIUM -> Icons.Default.Warning
                        WeaknessSeverity.LOW -> Icons.Default.Info
                    },
                    contentDescription = "Severidade: ${weakness.severity}",
                    tint = when (weakness.severity) {
                        WeaknessSeverity.HIGH -> colorFromHex(Tokens.Error)
                        WeaknessSeverity.MEDIUM -> colorFromHex(Tokens.Warning)
                        WeaknessSeverity.LOW -> colorFromHex(Tokens.Primary)
                    },
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = weakness.specificArea,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = weakness.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Tempo estimado para melhoria: ${weakness.estimatedTimeToImprove}h",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorFromHex(Tokens.Primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationsTab(
    recommendations: List<Recommendation>,
    onRecommendationViewed: (String) -> Unit
) {
    if (recommendations.isEmpty()) {
        EmptyStateCard(
            icon = Icons.Default.CheckCircle,
            title = "Tudo em ordem!",
            description = "Nenhuma recomendação específica no momento"
        )
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            recommendations.forEach { recommendation ->
                RecommendationCard(
                    recommendation = recommendation,
                    onViewed = { onRecommendationViewed(recommendation.id) }
                )
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: Recommendation,
    onViewed: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onViewed
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recommendation.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = recommendation.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
                
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
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            if (recommendation.actionItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Ações sugeridas:",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorFromHex(Tokens.Primary),
                    fontWeight = FontWeight.Medium
                )
                
                recommendation.actionItems.take(3).forEach { action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            tint = colorFromHex(Tokens.Neutral),
                            modifier = Modifier.size(8.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = action.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorFromHex(Tokens.Neutral)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressTab(progressTrend: List<ProgressPoint>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (progressTrend.isEmpty()) {
            EmptyStateCard(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = "Sem dados suficientes",
                description = "Complete mais sessões para ver seu progresso"
            )
        } else {
            // Gráfico de progresso
            SimpleChart(
                data = progressTrend,
                title = "Evolução da Precisão"
            )
            
            // Métricas de performance
            val currentAccuracy = progressTrend.lastOrNull()?.accuracy ?: 0.0
            val previousAccuracy = if (progressTrend.size >= 2) {
                progressTrend[progressTrend.size - 2].accuracy
            } else {
                currentAccuracy
            }
            val improvement = currentAccuracy - previousAccuracy
            
            PerformanceMetrics(
                accuracy = currentAccuracy,
                improvement = improvement,
                streak = 7 // Placeholder, seria calculado baseado nos dados reais
            )
            
            // Insights detalhados
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Tokens.CardRadius),
                colors = CardDefaults.cardColors(
                    containerColor = colorFromHex(Tokens.SurfaceElevated)
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Insights de Progresso",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val trend = if (improvement > 0) "melhorando" else if (improvement < 0) "declínio" else "estável"
                    val trendColor = when {
                        improvement > 0 -> colorFromHex(Tokens.Positive)
                        improvement < 0 -> colorFromHex(Tokens.Negative)
                        else -> colorFromHex(Tokens.Neutral)
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when {
                                improvement > 0 -> Icons.Default.TrendingUp
                                improvement < 0 -> Icons.Default.TrendingDown
                                else -> Icons.Default.TrendingFlat
                            },
                            contentDescription = trend,
                            tint = trendColor,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Sua performance está $trend",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Continue praticando para manter o progresso consistente!",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colorFromHex(Tokens.Primary),
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
private fun ErrorCard(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Tokens.ScreenPad),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.Error)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Erro",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = Color.White
                )
            }
        }
    }
}

// Helper functions
private fun getRankColor(rank: PlayerRank): Color {
    return when (rank) {
        PlayerRank.BRONZE -> Color(0xFF8D4004)
        PlayerRank.SILVER -> Color(0xFF9CA3AF)
        PlayerRank.GOLD -> Color(0xFFD97706)
        PlayerRank.PLATINUM -> Color(0xFF10B981)
        PlayerRank.DIAMOND -> Color(0xFF3B82F6)
        PlayerRank.MASTER -> Color(0xFF8B5CF6)
    }
}

private fun getRankIcon(rank: PlayerRank): androidx.compose.ui.graphics.vector.ImageVector {
    return when (rank) {
        PlayerRank.BRONZE, PlayerRank.SILVER -> Icons.Default.Shield
        PlayerRank.GOLD, PlayerRank.PLATINUM -> Icons.Default.Star
        PlayerRank.DIAMOND, PlayerRank.MASTER -> Icons.Default.Diamond
    }
}

private fun getMasteryLabel(mastery: MasteryLevel): String {
    return when (mastery) {
        MasteryLevel.BEGINNER -> "Iniciante"
        MasteryLevel.INTERMEDIATE -> "Intermediário"
        MasteryLevel.ADVANCED -> "Avançado"
        MasteryLevel.EXPERT -> "Expert"
    }
}
