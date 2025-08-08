package com.pokertrainer.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pokertrainer.data.SampleData
import com.pokertrainer.ui.components.BottomPillBar
import com.pokertrainer.ui.screens.*
import com.pokertrainer.ui.screens.practice.HandReadingScreen
import com.pokertrainer.ui.screens.practice.PostflopDrillScreen
import com.pokertrainer.ui.screens.practice.PracticeHomeScreen
import com.pokertrainer.ui.screens.practice.PreflopDrillScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokerTrainerApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Verifica se está em uma tela que deve mostrar a bottom bar
    val showBottomBar = currentRoute in listOf(Screen.Home.route, Screen.Schedule.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomPillBar(currentRoute, navController)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    user = SampleData.user,
                    onNavigateToPractice = {
                        navController.navigate(Screen.PracticeHome.route)
                    },
                    onNavigateToStats = {
                        navController.navigate(Screen.Stats.route)
                    },
                    onNavigateToAnalysis = {
                        navController.navigate(Screen.PerformanceAnalysis.route)
                    },
                    onNavigateToNotifications = {
                        navController.navigate(Screen.Notifications.route)
                    },
                    onNavigateToTutorials = {
                        navController.navigate(Screen.Tutorials.route)
                    }
                )
            }
            composable(Screen.Schedule.route) {
                ScheduleScreen(sessions = SampleData.sessions)
            }
            composable(Screen.Stats.route) {
                StatsScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Training.route) {
                TrainingScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Gamification.route) {
                GamificationScreen()
            }
            composable(Screen.Notifications.route) {
                NotificationScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Tutorials.route) {
                TutorialScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }

            // Novas telas de prática
            composable(Screen.PracticeHome.route) {
                PracticeHomeScreen(
                    onDrillClick = { drill ->
                        navController.navigate(Screen.PreflopDrill.createRoute(drill.id))
                    },
                    onCategoryClick = { category ->
                        // Navegação por categoria será implementada posteriormente
                    },
                    onPostflopStart = { sessionId ->
                        navController.navigate(Screen.PostflopDrill.createRoute(sessionId))
                    },
                    onHandReadingStart = {
                        navController.navigate(Screen.HandReading.route)
                    }
                )
            }

            composable(
                route = Screen.PreflopDrill.route,
                arguments = listOf(
                    navArgument("drillId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val drillId = backStackEntry.arguments?.getInt("drillId") ?: 1

                PreflopDrillScreen(
                    drillId = drillId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onCompleteClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = Screen.PostflopDrill.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""

                PostflopDrillScreen(
                    sessionId = sessionId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onCompleteClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.HandReading.route) {
                HandReadingScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onCompleteClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.PerformanceAnalysis.route) {
                PerformanceAnalysisScreen()
            }
        }
    }
}
