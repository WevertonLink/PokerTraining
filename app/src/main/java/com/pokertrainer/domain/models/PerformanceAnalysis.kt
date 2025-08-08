package com.pokertrainer.domain.models

import java.time.LocalDateTime

/**
 * Representa a análise de performance do jogador
 */
data class PerformanceAnalysis(
    val playerId: String,
    val period: AnalysisPeriod,
    val overallStats: OverallStats,
    val categoryPerformance: Map<DrillCategory, CategoryStats>,
    val weaknesses: List<WeaknessArea>,
    val recommendations: List<Recommendation>,
    val progressTrend: List<ProgressPoint>,
    val lastUpdated: LocalDateTime
)

/**
 * Estatísticas gerais do jogador
 */
data class OverallStats(
    val totalDrillsCompleted: Int,
    val averageAccuracy: Double, // 0.0 to 1.0
    val totalTimeSpent: Long, // em milissegundos
    val sessionsCompleted: Int,
    val currentStreak: Int, // dias consecutivos
    val bestStreak: Int,
    val improvementRate: Double, // taxa de melhoria semanal
    val rank: PlayerRank
)

/**
 * Performance por categoria de drill
 */
data class CategoryStats(
    val category: DrillCategory,
    val accuracy: Double,
    val averageTime: Long, // tempo médio por questão
    val questionsAnswered: Int,
    val difficultyLevel: Int, // 1-5
    val masteryLevel: MasteryLevel,
    val lastPracticed: LocalDateTime?
)

/**
 * Área de fraqueza identificada
 */
data class WeaknessArea(
    val id: String,
    val category: DrillCategory,
    val specificArea: String, // ex: "3-bet ranges from UTG"
    val severity: WeaknessSeverity,
    val description: String,
    val suggestedDrills: List<Int>, // IDs dos drills recomendados
    val estimatedTimeToImprove: Long // em horas
)

/**
 * Recomendação personalizada
 */
data class Recommendation(
    val id: String,
    val type: RecommendationType,
    val title: String,
    val description: String,
    val priority: RecommendationPriority,
    val estimatedImpact: Double, // 0.0 to 1.0
    val actionItems: List<ActionItem>,
    val targetCategory: DrillCategory?
)

/**
 * Item de ação específico
 */
data class ActionItem(
    val description: String,
    val drillId: Int?,
    val estimatedTime: Long, // em minutos
    val isCompleted: Boolean = false
)

/**
 * Ponto de progresso para gráficos
 */
data class ProgressPoint(
    val date: LocalDateTime,
    val accuracy: Double,
    val category: DrillCategory?,
    val sessionsCompleted: Int
)

/**
 * Período de análise
 */
enum class AnalysisPeriod {
    LAST_7_DAYS,
    LAST_30_DAYS,
    LAST_3_MONTHS,
    ALL_TIME
}

/**
 * Nível de maestria
 */
enum class MasteryLevel {
    BEGINNER,     // < 60% accuracy
    INTERMEDIATE, // 60-80% accuracy  
    ADVANCED,     // 80-90% accuracy
    EXPERT        // > 90% accuracy
}

/**
 * Severidade da fraqueza
 */
enum class WeaknessSeverity {
    LOW,    // Pequenos ajustes necessários
    MEDIUM, // Prática focada recomendada
    HIGH    // Área crítica que precisa de atenção
}

/**
 * Tipo de recomendação
 */
enum class RecommendationType {
    DRILL_PRACTICE,      // Praticar drills específicos
    STUDY_MATERIAL,      // Estudar material teórico
    FREQUENCY_INCREASE,  // Aumentar frequência de prática
    DIFFICULTY_ADJUST,   // Ajustar nível de dificuldade
    FOCUS_AREA          // Focar em área específica
}

/**
 * Prioridade da recomendação
 */
enum class RecommendationPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Rank do jogador
 */
enum class PlayerRank {
    BRONZE,
    SILVER,
    GOLD,
    PLATINUM,
    DIAMOND,
    MASTER
}
