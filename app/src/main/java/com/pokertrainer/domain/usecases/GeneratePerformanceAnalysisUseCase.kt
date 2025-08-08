package com.pokertrainer.domain.usecases

import com.pokertrainer.data.repository.TrainingRepository
import com.pokertrainer.domain.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random

/**
 * Use case para gerar análise de performance personalizada do jogador
 */
class GeneratePerformanceAnalysisUseCase(
    private val repository: TrainingRepository
) {
    
    suspend operator fun invoke(
        playerId: String,
        period: AnalysisPeriod = AnalysisPeriod.LAST_30_DAYS
    ): Result<PerformanceAnalysis> = withContext(Dispatchers.IO) {
        try {
            // Buscar histórico de sessões do jogador
            val sessions = repository.getTrainingHistory()
            val filteredSessions = filterSessionsByPeriod(sessions, period)
            
            // Gerar estatísticas gerais
            val overallStats = generateOverallStats(filteredSessions)
            
            // Analisar performance por categoria
            val categoryPerformance = generateCategoryPerformance(filteredSessions)
            
            // Identificar áreas de fraqueza
            val weaknesses = identifyWeaknesses(categoryPerformance, filteredSessions)
            
            // Gerar recomendações personalizadas
            val recommendations = generateRecommendations(weaknesses, categoryPerformance)
            
            // Calcular tendência de progresso
            val progressTrend = calculateProgressTrend(filteredSessions)
            
            val analysis = PerformanceAnalysis(
                playerId = playerId,
                period = period,
                overallStats = overallStats,
                categoryPerformance = categoryPerformance,
                weaknesses = weaknesses,
                recommendations = recommendations,
                progressTrend = progressTrend,
                lastUpdated = LocalDateTime.now()
            )
            
            Result.success(analysis)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun filterSessionsByPeriod(
        sessions: List<TrainingSession>,
        period: AnalysisPeriod
    ): List<TrainingSession> {
        val cutoffDate = when (period) {
            AnalysisPeriod.LAST_7_DAYS -> LocalDateTime.now().minusDays(7)
            AnalysisPeriod.LAST_30_DAYS -> LocalDateTime.now().minusDays(30)
            AnalysisPeriod.LAST_3_MONTHS -> LocalDateTime.now().minusMonths(3)
            AnalysisPeriod.ALL_TIME -> LocalDateTime.of(2020, 1, 1, 0, 0)
        }
        
        return sessions.filter { session ->
            val sessionDate = LocalDateTime.ofEpochSecond(session.startTime / 1000, 0, java.time.ZoneOffset.UTC)
            sessionDate.isAfter(cutoffDate)
        }
    }
    
    private fun generateOverallStats(sessions: List<TrainingSession>): OverallStats {
        val totalQuestions = sessions.sumOf { it.totalQuestions }
        val totalCorrect = sessions.sumOf { it.totalCorrect }
        val accuracy = if (totalQuestions > 0) totalCorrect.toDouble() / totalQuestions else 0.0
        
        val totalTime = sessions.sumOf { session ->
            (session.endTime ?: System.currentTimeMillis()) - session.startTime
        }
        
        // Calcular streak (simplificado para demo)
        val currentStreak = calculateStreak(sessions)
        
        return OverallStats(
            totalDrillsCompleted = sessions.size,
            averageAccuracy = accuracy,
            totalTimeSpent = totalTime,
            sessionsCompleted = sessions.size,
            currentStreak = currentStreak,
            bestStreak = currentStreak + Random.nextInt(5), // Simulado
            improvementRate = calculateImprovementRate(sessions),
            rank = determineRank(accuracy)
        )
    }
    
    private fun generateCategoryPerformance(
        sessions: List<TrainingSession>
    ): Map<DrillCategory, CategoryStats> {
        return DrillCategory.values().associateWith { category ->
            val categorySessions = sessions.filter { session ->
                // Assumindo que temos uma maneira de identificar a categoria
                // Por simplicidade, vamos distribuir aleatoriamente
                session.id.hashCode() % DrillCategory.values().size == category.ordinal
            }
            
            val totalQuestions = categorySessions.sumOf { it.totalQuestions }
            val totalCorrect = categorySessions.sumOf { it.totalCorrect }
            val accuracy = if (totalQuestions > 0) totalCorrect.toDouble() / totalQuestions else 0.0
            
            val averageTime = if (categorySessions.isNotEmpty()) {
                categorySessions.map { session ->
                    (session.endTime ?: System.currentTimeMillis()) - session.startTime
                }.average().toLong() / categorySessions.size
            } else 0L
            
            CategoryStats(
                category = category,
                accuracy = accuracy,
                averageTime = averageTime,
                questionsAnswered = totalQuestions,
                difficultyLevel = determineDifficultyLevel(accuracy),
                masteryLevel = determineMasteryLevel(accuracy),
                lastPracticed = categorySessions.maxOfOrNull { session ->
                    LocalDateTime.ofEpochSecond(session.startTime / 1000, 0, java.time.ZoneOffset.UTC)
                }
            )
        }
    }
    
    private fun identifyWeaknesses(
        categoryPerformance: Map<DrillCategory, CategoryStats>,
        sessions: List<TrainingSession>
    ): List<WeaknessArea> {
        return categoryPerformance.filter { (_, stats) ->
            stats.accuracy < 0.7 // Considerar fraqueza se accuracy < 70%
        }.map { (category, stats) ->
            WeaknessArea(
                id = "weakness_${category.name.lowercase()}",
                category = category,
                specificArea = getSpecificWeaknessArea(category, stats),
                severity = when {
                    stats.accuracy < 0.5 -> WeaknessSeverity.HIGH
                    stats.accuracy < 0.6 -> WeaknessSeverity.MEDIUM
                    else -> WeaknessSeverity.LOW
                },
                description = generateWeaknessDescription(category, stats),
                suggestedDrills = getSuggestedDrills(category),
                estimatedTimeToImprove = estimateImprovementTime(stats)
            )
        }
    }
    
    private fun generateRecommendations(
        weaknesses: List<WeaknessArea>,
        categoryPerformance: Map<DrillCategory, CategoryStats>
    ): List<Recommendation> {
        val recommendations = mutableListOf<Recommendation>()
        
        // Recomendações baseadas em fraquezas
        weaknesses.forEach { weakness ->
            recommendations.add(
                Recommendation(
                    id = "rec_${weakness.id}",
                    type = RecommendationType.DRILL_PRACTICE,
                    title = "Melhorar ${weakness.category.name}",
                    description = "Focar na prática de ${weakness.specificArea}",
                    priority = when (weakness.severity) {
                        WeaknessSeverity.HIGH -> RecommendationPriority.HIGH
                        WeaknessSeverity.MEDIUM -> RecommendationPriority.MEDIUM
                        WeaknessSeverity.LOW -> RecommendationPriority.LOW
                    },
                    estimatedImpact = calculateEstimatedImpact(weakness),
                    actionItems = generateActionItems(weakness),
                    targetCategory = weakness.category
                )
            )
        }
        
        // Recomendação geral de frequência se performance está baixa
        val overallAccuracy = categoryPerformance.values.map { it.accuracy }.average()
        if (overallAccuracy < 0.75) {
            recommendations.add(
                Recommendation(
                    id = "rec_frequency",
                    type = RecommendationType.FREQUENCY_INCREASE,
                    title = "Aumentar Frequência de Prática",
                    description = "Praticar mais regularmente para melhorar consistência",
                    priority = RecommendationPriority.MEDIUM,
                    estimatedImpact = 0.3,
                    actionItems = listOf(
                        ActionItem("Praticar pelo menos 30 minutos por dia", null, 30),
                        ActionItem("Completar 3 drills por sessão", null, 20)
                    ),
                    targetCategory = null
                )
            )
        }
        
        return recommendations
    }
    
    private fun calculateProgressTrend(sessions: List<TrainingSession>): List<ProgressPoint> {
        return sessions.sortedBy { it.startTime }.map { session ->
            val accuracy = if (session.totalQuestions > 0) {
                session.totalCorrect.toDouble() / session.totalQuestions
            } else 0.0
            
            ProgressPoint(
                date = LocalDateTime.ofEpochSecond(session.startTime / 1000, 0, java.time.ZoneOffset.UTC),
                accuracy = accuracy,
                category = null, // Poderia ser específico por categoria
                sessionsCompleted = 1
            )
        }
    }
    
    // Funções auxiliares
    private fun calculateStreak(sessions: List<TrainingSession>): Int {
        // Implementação simplificada
        return minOf(sessions.size, 7)
    }
    
    private fun calculateImprovementRate(sessions: List<TrainingSession>): Double {
        if (sessions.size < 2) return 0.0
        
        val recent = sessions.takeLast(sessions.size / 2)
        val older = sessions.take(sessions.size / 2)
        
        val recentAccuracy = recent.map { 
            if (it.totalQuestions > 0) it.totalCorrect.toDouble() / it.totalQuestions else 0.0 
        }.average()
        
        val olderAccuracy = older.map { 
            if (it.totalQuestions > 0) it.totalCorrect.toDouble() / it.totalQuestions else 0.0 
        }.average()
        
        return recentAccuracy - olderAccuracy
    }
    
    private fun determineRank(accuracy: Double): PlayerRank {
        return when {
            accuracy >= 0.95 -> PlayerRank.MASTER
            accuracy >= 0.90 -> PlayerRank.DIAMOND
            accuracy >= 0.80 -> PlayerRank.PLATINUM
            accuracy >= 0.70 -> PlayerRank.GOLD
            accuracy >= 0.60 -> PlayerRank.SILVER
            else -> PlayerRank.BRONZE
        }
    }
    
    private fun determineDifficultyLevel(accuracy: Double): Int {
        return when {
            accuracy >= 0.90 -> 5
            accuracy >= 0.80 -> 4
            accuracy >= 0.70 -> 3
            accuracy >= 0.60 -> 2
            else -> 1
        }
    }
    
    private fun determineMasteryLevel(accuracy: Double): MasteryLevel {
        return when {
            accuracy >= 0.90 -> MasteryLevel.EXPERT
            accuracy >= 0.80 -> MasteryLevel.ADVANCED
            accuracy >= 0.60 -> MasteryLevel.INTERMEDIATE
            else -> MasteryLevel.BEGINNER
        }
    }
    
    private fun getSpecificWeaknessArea(category: DrillCategory, stats: CategoryStats): String {
        return when (category) {
            DrillCategory.PREFLOP -> "Ranges de abertura em posições tardias"
            DrillCategory.POSTFLOP -> "Decisões de continuation bet"
            DrillCategory.HAND_READING -> "Identificação de bluffs"
            DrillCategory.BANKROLL_MANAGEMENT -> "Gestão de risco em torneios"
        }
    }
    
    private fun generateWeaknessDescription(category: DrillCategory, stats: CategoryStats): String {
        return "Performance abaixo da média em ${category.name} com ${(stats.accuracy * 100).toInt()}% de acertos"
    }
    
    private fun getSuggestedDrills(category: DrillCategory): List<Int> {
        // IDs dos drills recomendados por categoria
        return when (category) {
            DrillCategory.PREFLOP -> listOf(1, 2, 3)
            DrillCategory.POSTFLOP -> listOf(4, 5, 6)
            DrillCategory.HAND_READING -> listOf(7, 8, 9)
            DrillCategory.BANKROLL_MANAGEMENT -> listOf(10, 11, 12)
        }
    }
    
    private fun estimateImprovementTime(stats: CategoryStats): Long {
        // Tempo estimado em horas baseado na dificuldade
        return when {
            stats.accuracy < 0.5 -> 20L
            stats.accuracy < 0.6 -> 15L
            else -> 10L
        }
    }
    
    private fun calculateEstimatedImpact(weakness: WeaknessArea): Double {
        return when (weakness.severity) {
            WeaknessSeverity.HIGH -> 0.7
            WeaknessSeverity.MEDIUM -> 0.5
            WeaknessSeverity.LOW -> 0.3
        }
    }
    
    private fun generateActionItems(weakness: WeaknessArea): List<ActionItem> {
        return weakness.suggestedDrills.map { drillId ->
            ActionItem(
                description = "Completar drill #$drillId",
                drillId = drillId,
                estimatedTime = 25L
            )
        }
    }
}
