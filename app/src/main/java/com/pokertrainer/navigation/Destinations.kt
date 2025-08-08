package com.pokertrainer.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Stats : Screen("stats")
    object Training : Screen("training")
    object PracticeHome : Screen("practice_home")
    object PreflopDrill : Screen("preflop_drill/{drillId}") {
        fun createRoute(drillId: Int) = "preflop_drill/$drillId"
    }
}
