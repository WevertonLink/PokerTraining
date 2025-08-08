package com.pokertrainer.data.repository

import com.pokertrainer.domain.models.*

interface TrainingRepository {
    suspend fun getDrillById(drillId: Int): TrainingDrill?
    suspend fun getAllDrills(): List<TrainingDrill>
    suspend fun getDrillsByCategory(category: DrillCategory): List<TrainingDrill>
    suspend fun saveTrainingSession(session: TrainingSession)
    suspend fun getTrainingHistory(): List<TrainingSession>
    fun getStandardRange(position: Position, action: ActionType): List<String>
    suspend fun updateDrillProgress(drillId: Int, completed: Boolean)
}
