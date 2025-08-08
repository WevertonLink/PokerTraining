package com.pokertrainer.data

import com.pokertrainer.domain.models.*

object SampleData {
    val user = User(
        name = "Alex",
        bankroll = 12580.50,
        handsSolved = 1247,
        weeklyDelta = 89
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
