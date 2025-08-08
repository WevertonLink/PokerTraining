package com.pokertrainer.data.repository

import com.pokertrainer.data.SampleData
import com.pokertrainer.domain.models.*
import kotlinx.coroutines.delay

class TrainingRepositoryImpl : TrainingRepository {

    private val drills = mutableListOf<TrainingDrill>().apply {
        addAll(SampleData.preflopDrills)
    }

    private val sessions = mutableListOf<TrainingSession>()

    override suspend fun getDrillById(drillId: Int): TrainingDrill? {
        delay(100) // Simula operação assíncrona
        return drills.find { it.id == drillId }
    }

    override suspend fun getAllDrills(): List<TrainingDrill> {
        delay(100)
        return drills.toList()
    }

    override suspend fun getDrillsByCategory(category: DrillCategory): List<TrainingDrill> {
        delay(100)
        return drills.filter { it.category == category }
    }

    override suspend fun saveTrainingSession(session: TrainingSession) {
        delay(100)
        sessions.add(session)
    }

    override suspend fun getTrainingHistory(): List<TrainingSession> {
        delay(100)
        return sessions.toList()
    }

    override suspend fun updateDrillProgress(drillId: Int, completed: Boolean) {
        delay(100)
        val index = drills.indexOfFirst { it.id == drillId }
        if (index != -1) {
            drills[index] = drills[index].copy(completed = completed)
        }
    }

    override fun getStandardRange(position: Position, action: ActionType): List<String> {
        return when (position) {
            Position.UTG -> when (action) {
                ActionType.UNOPENED -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "AKs", "AKo", "AQs", "AQo", "AJs", "AJo")
                ActionType.THREE_BET -> listOf("AA", "KK", "QQ", "JJ", "AKs", "AKo", "AQs")
                else -> listOf("AA", "KK", "QQ", "AKs", "AKo")
            }
            Position.MP -> when (action) {
                ActionType.UNOPENED -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "77", "AKs", "AKo", "AQs", "AQo", "AJs", "AJo", "ATs", "KQs", "KQo")
                ActionType.THREE_BET -> listOf("AA", "KK", "QQ", "JJ", "TT", "AKs", "AKo", "AQs")
                else -> listOf("AA", "KK", "QQ", "AKs", "AKo")
            }
            Position.CO -> when (action) {
                ActionType.UNOPENED -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "77", "66", "AKs", "AKo", "AQs", "AQo", "AJs", "AJo", "ATs", "A9s", "KQs", "KQo", "KJs", "KJo", "QJs")
                ActionType.THREE_BET -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "AKs", "AKo", "AQs", "AJs", "KQs")
                ActionType.ONE_LIMP -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "77", "AKs", "AKo", "AQs", "AQo", "AJs", "AJo", "ATs", "A9s", "KQs", "KQo", "KJs", "KJo")
                else -> listOf("AA", "KK", "QQ", "AKs", "AKo")
            }
            Position.BTN -> when (action) {
                ActionType.UNOPENED -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "77", "66", "55", "44", "33", "22", "AKs", "AKo", "AQs", "AQo", "AJs", "AJo", "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s", "KQs", "KQo", "KJs", "KJo", "KTs", "K9s", "QJs", "QJo", "QTs", "Q9s", "JTs", "J9s", "T9s", "98s", "87s", "76s", "65s")
                ActionType.THREE_BET -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "AKs", "AKo", "AQs", "AJs", "ATs", "A5s", "A4s", "KQs", "KJs", "QJs", "J9s", "T8s", "97s", "86s", "75s", "64s", "54s")
                else -> listOf("AA", "KK", "QQ", "JJ", "AKs", "AKo", "AQs")
            }
            Position.SB -> when (action) {
                ActionType.UNOPENED -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "77", "66", "55", "44", "AKs", "AKo", "AQs", "AQo", "AJs", "AJo", "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s", "KQs", "KQo", "KJs", "KJo", "KTs", "K9s", "K8s", "K7s", "K6s", "K5s", "K4s", "K3s", "K2s", "QJs", "QJo", "QTs", "Q9s", "Q8s", "JTs", "J9s", "J8s", "T9s", "T8s", "98s", "97s", "87s", "86s", "76s", "75s", "65s", "64s", "54s", "53s", "43s")
                ActionType.THREE_BET -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "77", "AKs", "AKo", "AQs", "AJs", "ATs", "A9s", "A8s", "A7s", "A6s", "A5s", "A4s", "A3s", "A2s", "KQs", "KJs", "KTs", "K9s", "QJs", "QTs", "JTs", "T9s", "98s", "87s", "76s", "65s")
                else -> listOf("AA", "KK", "QQ", "JJ", "AKs", "AKo")
            }
            Position.BB -> when (action) {
                ActionType.THREE_BET -> listOf("AA", "KK", "QQ", "JJ", "TT", "99", "88", "AKs", "AKo", "AQs", "AJs", "ATs", "A9s", "A5s", "A4s", "A3s", "A2s", "KQs", "KJs", "KTs", "K9s", "QJs", "QTs", "JTs", "J9s", "T9s", "98s", "87s", "76s", "65s", "54s")
                ActionType.FOUR_BET_PLUS -> listOf("AA", "KK", "QQ", "JJ", "AKs", "AKo", "AQs", "A5s", "A4s")
                else -> listOf("AA", "KK", "QQ", "JJ", "TT", "AKs", "AKo")
            }
        }
    }
}
