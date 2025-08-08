package com.pokertrainer.data

data class User(
    val name: String,
    val bankroll: Double,
    val handsSolved: Int,
    val weeklyDelta: Int
)

data class TrainingModule(
    val id: String,
    val title: String,
    val description: String,
    val duration: String,
    val difficulty: String,
    val isCompleted: Boolean
)

data class StudyGroup(
    val id: String,
    val name: String,
    val members: Int,
    val nextSession: String
)

data class Opponent(
    val id: String,
    val name: String,
    val avatar: String,
    val winRate: Double,
    val lastPlayed: String
)

data class StatModule(
    val title: String,
    val value: String,
    val progress: Float,
    val color: String
)
