package com.pokertrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokertrainer.data.TrainingSession
import com.pokertrainer.data.SampleData
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun ScheduleScreen(sessions: List<TrainingSession>) {
    val dates = listOf("Hoje", "Amanhã", "Sex", "Sáb", "Dom")
    var selectedDate by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        TopGreetingBar("Alex")
        SectionHeader("Agendar treinos")
        DateChips(dates, selectedDate) { selectedDate = it }
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        SectionHeader("Sessões/Desafios")
        SessionList(sessions)
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        SectionHeader("Grupos de estudo")
        StudyGroups()
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        SectionHeader("Oponentes recentes")
        RecentOpponents()
        
        // Add some bottom padding for the bottom bar
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun SessionList(sessions: List<TrainingSession>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing)
    ) {
        sessions.forEach { session ->
            SessionCard(session)
        }
    }
}

@Composable
fun SessionCard(session: TrainingSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (session.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                contentDescription = if (session.isCompleted) "Concluído" else "Iniciar",
                tint = if (session.isCompleted) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Primary),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = session.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorFromHex(Tokens.Neutral)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = session.duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${session.difficulty}",
                        style = MaterialTheme.typography.labelSmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
            }
        }
    }
}

@Composable
fun StudyGroups() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing)
    ) {
        SampleData.studyGroups.forEach { group ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Tokens.CardRadius),
                colors = CardDefaults.cardColors(
                    containerColor = colorFromHex(Tokens.SurfaceElevated)
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "${group.members} membros",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorFromHex(Tokens.Neutral)
                        )
                    }
                    Text(
                        text = group.nextSession,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorFromHex(Tokens.Primary)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentOpponents() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing)
    ) {
        SampleData.opponents.forEach { opponent ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Tokens.CardRadius),
                colors = CardDefaults.cardColors(
                    containerColor = colorFromHex(Tokens.SurfaceElevated)
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = opponent.avatar,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = opponent.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Win rate: ${opponent.winRate}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorFromHex(Tokens.Positive)
                        )
                    }
                    
                    Text(
                        text = opponent.lastPlayed,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
            }
        }
    }
}
