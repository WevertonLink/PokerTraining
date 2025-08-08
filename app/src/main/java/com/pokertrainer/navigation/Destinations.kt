package com.pokertrainer.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Dashboard : Screen("dashboard")
    object Schedule : Screen("schedule")
    object Stats : Screen("stats")
    object Training : Screen("training")
    object Gamification : Screen("gamification")
    object Notifications : Screen("notifications")
    object Tutorials : Screen("tutorials")
    object PracticeHome : Screen("practice_home")
    object PerformanceAnalysis : Screen("performance_analysis")
    object PreflopDrill : Screen("preflop_drill/{drillId}") {
        fun createRoute(drillId: Int) = "preflop_drill/$drillId"
    }
    object PostflopDrill : Screen("postflop_drill/{sessionId}") {
        fun createRoute(sessionId: String) = "postflop_drill/$sessionId"
    }
    object HandReading : Screen("hand_reading")
}
