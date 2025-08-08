package com.pokertrainer.data

import com.pokertrainer.domain.models.*
import java.time.LocalDateTime

object SampleData {
    val user = User(
        name = "Alex",
        bankroll = 12580.50,
        handsSolved = 1247,
        weeklyDelta = 89
    )

    // Dados simplificados para análise de performance
    val sampleAnalysis = PerformanceAnalysis(
        playerId = "user123",
        period = AnalysisPeriod.LAST_30_DAYS,
        lastUpdated = LocalDateTime.now(),
        overallStats = OverallStats(
            totalDrillsCompleted = 245,
            averageAccuracy = 0.78,
            totalTimeSpent = 85200000L, // 1420 minutos em ms
            sessionsCompleted = 24,
            currentStreak = 7,
            bestStreak = 12,
            improvementRate = 0.12,
            rank = PlayerRank.GOLD
        ),
        categoryPerformance = mapOf(
            DrillCategory.PREFLOP to CategoryStats(
                category = DrillCategory.PREFLOP,
                accuracy = 0.82,
                averageTime = 4500L,
                questionsAnswered = 120,
                difficultyLevel = 3,
                masteryLevel = MasteryLevel.ADVANCED,
                lastPracticed = LocalDateTime.now().minusDays(1)
            ),
            DrillCategory.POSTFLOP to CategoryStats(
                category = DrillCategory.POSTFLOP,
                accuracy = 0.74,
                averageTime = 6200L,
                questionsAnswered = 95,
                difficultyLevel = 4,
                masteryLevel = MasteryLevel.INTERMEDIATE,
                lastPracticed = LocalDateTime.now().minusDays(2)
            ),
            DrillCategory.HAND_READING to CategoryStats(
                category = DrillCategory.HAND_READING,
                accuracy = 0.67,
                averageTime = 8100L,
                questionsAnswered = 30,
                difficultyLevel = 5,
                masteryLevel = MasteryLevel.BEGINNER,
                lastPracticed = LocalDateTime.now().minusDays(5)
            )
        ),
        weaknesses = listOf(
            WeaknessArea(
                id = "weak1",
                category = DrillCategory.HAND_READING,
                specificArea = "Bluff catching em river",
                severity = WeaknessSeverity.HIGH,
                description = "Dificuldade em identificar quando o oponente pode estar blufando no river",
                suggestedDrills = listOf(1, 2),
                estimatedTimeToImprove = 15L
            ),
            WeaknessArea(
                id = "weak2",
                category = DrillCategory.POSTFLOP,
                specificArea = "C-bet sizing",
                severity = WeaknessSeverity.MEDIUM,
                description = "Inconsistência no sizing de continuation bets",
                suggestedDrills = listOf(3, 4),
                estimatedTimeToImprove = 8L
            )
        ),
        recommendations = listOf(
            Recommendation(
                id = "rec1",
                type = RecommendationType.DRILL_PRACTICE,
                title = "Foque em spots de river",
                description = "Dedique mais tempo estudando situações de river para melhorar sua capacidade de bluff catching",
                priority = RecommendationPriority.HIGH,
                estimatedImpact = 0.8,
                targetCategory = DrillCategory.HAND_READING,
                actionItems = listOf(
                    ActionItem(
                        description = "Complete 20 drills de river play",
                        drillId = 1,
                        estimatedTime = 60L
                    ),
                    ActionItem(
                        description = "Revise conceitos de pot odds em river",
                        drillId = 2,
                        estimatedTime = 30L
                    )
                )
            ),
            Recommendation(
                id = "rec2",
                type = RecommendationType.STUDY_MATERIAL,
                title = "Pratique sizing de c-bet",
                description = "Trabalhe na consistência dos sizings de continuation bet",
                priority = RecommendationPriority.MEDIUM,
                estimatedImpact = 0.6,
                targetCategory = DrillCategory.POSTFLOP,
                actionItems = listOf(
                    ActionItem(
                        description = "Estude diferentes sizes para diferentes boards",
                        drillId = 3,
                        estimatedTime = 45L
                    )
                )
            )
        ),
        progressTrend = listOf(
            ProgressPoint(
                date = LocalDateTime.now().minusDays(30),
                accuracy = 0.68,
                category = DrillCategory.PREFLOP,
                sessionsCompleted = 3
            ),
            ProgressPoint(
                date = LocalDateTime.now().minusDays(25),
                accuracy = 0.71,
                category = DrillCategory.PREFLOP,
                sessionsCompleted = 5
            ),
            ProgressPoint(
                date = LocalDateTime.now().minusDays(20),
                accuracy = 0.69,
                category = DrillCategory.PREFLOP,
                sessionsCompleted = 7
            ),
            ProgressPoint(
                date = LocalDateTime.now().minusDays(15),
                accuracy = 0.74,
                category = DrillCategory.PREFLOP,
                sessionsCompleted = 10
            ),
            ProgressPoint(
                date = LocalDateTime.now().minusDays(10),
                accuracy = 0.76,
                category = DrillCategory.PREFLOP,
                sessionsCompleted = 15
            ),
            ProgressPoint(
                date = LocalDateTime.now().minusDays(5),
                accuracy = 0.78,
                category = DrillCategory.PREFLOP,
                sessionsCompleted = 20
            ),
            ProgressPoint(
                date = LocalDateTime.now(),
                accuracy = 0.78,
                category = DrillCategory.PREFLOP,
                sessionsCompleted = 24
            )
        )
    )

    val sessions = listOf(
        TrainingModule(
            id = "1",
            title = "Pre-flop Fundamentals",
            description = "Master opening ranges and 3-bet strategy",
            duration = "45 min",
            difficulty = "Iniciante",
            isCompleted = false
        ),
        TrainingModule(
            id = "2",
            title = "Post-flop C-betting",
            description = "When and how to continuation bet",
            duration = "60 min",
            difficulty = "Intermediário",
            isCompleted = true
        ),
        TrainingModule(
            id = "3",
            title = "Bluff Catching",
            description = "Identify and call light against bluffs",
            duration = "50 min",
            difficulty = "Avançado",
            isCompleted = false
        )
    )

    val studyGroups = listOf(
        StudyGroup(
            id = "1",
            name = "Crushers NL200",
            members = 12,
            nextSession = "Hoje 20:00"
        ),
        StudyGroup(
            id = "2",
            name = "MTT Wizards",
            members = 8,
            nextSession = "Amanhã 19:30"
        )
    )

    val opponents = listOf(
        Opponent(
            id = "1",
            name = "PokerShark91",
            avatar = "🦈",
            winRate = 65.2,
            lastPlayed = "2h atrás"
        ),
        Opponent(
            id = "2",
            name = "BluffMaster",
            avatar = "🃏",
            winRate = 58.7,
            lastPlayed = "1 dia"
        ),
        Opponent(
            id = "3",
            name = "TightRock",
            avatar = "🗿",
            winRate = 71.3,
            lastPlayed = "3 dias"
        )
    )

    val statModules = listOf(
        StatModule("VPIP", "24%", 0.6f, "#7C5CFF"),
        StatModule("PFR", "18%", 0.45f, "#27D17F"),
        StatModule("3-Bet", "8%", 0.32f, "#FF6969"),
        StatModule("AF", "2.1", 0.7f, "#FFB800")
    )

    // Novos dados para drills de poker
    val preflopDrills = listOf(
        TrainingDrill(
            id = 1,
            title = "UTG Opening Ranges",
            description = "Aprenda os ranges de abertura de Under The Gun",
            category = DrillCategory.PREFLOP,
            difficulty = 2,
            duration = 15,
            scenarios = listOf(
                DrillScenario(
                    position = Position.UTG,
                    action = ActionType.UNOPENED,
                    correctRanges = listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "AKs", "AKo", "AQs", "AQo", "AJs", "AJo"),
                    description = "Range de abertura padrão UTG 6-max"
                )
            ),
            completed = false
        ),
        TrainingDrill(
            id = 2,
            title = "Button vs BB 3-bet",
            description = "Como responder a 3-bets do Big Blind",
            category = DrillCategory.PREFLOP,
            difficulty = 4,
            duration = 20,
            scenarios = listOf(
                DrillScenario(
                    position = Position.BTN,
                    action = ActionType.THREE_BET,
                    correctRanges = listOf("AA", "KK", "QQ", "JJ", "AKs", "AKo", "AQs"),
                    description = "4-bet for value contra 3-bet do BB"
                )
            ),
            completed = false
        ),
        TrainingDrill(
            id = 3,
            title = "Cutoff Isolation",
            description = "Isolar limpers da posição Cutoff",
            category = DrillCategory.PREFLOP,
            difficulty = 3,
            duration = 18,
            scenarios = listOf(
                DrillScenario(
                    position = Position.CO,
                    action = ActionType.ONE_LIMP,
                    correctRanges = listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "77", "AKs", "AKo", "AQs", "AQo", "AJs", "AJo", "ATs", "A9s", "KQs", "KQo", "KJs", "KJo"),
                    description = "Range de isolamento vs 1 limper"
                )
            ),
            completed = true
        )
    )
}
