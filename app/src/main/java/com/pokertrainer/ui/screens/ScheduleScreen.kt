package com.pokertrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokertrainer.data.TrainingModule
import com.pokertrainer.data.SampleData
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex
import com.pokertrainer.ui.viewmodels.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun ScheduleScreen(
    sessions: List<TrainingModule>,
    viewModel: ScheduleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        TopGreetingBar("Alex")
        
        // Enhanced Date Selector
        SectionHeader("Agenda Inteligente")
        SmartDateSelector(
            selectedDate = uiState.selectedDate,
            onDateSelect = { viewModel.selectDate(it) }
        )
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        
        // AI Suggestions Card
        if (uiState.suggestedSessions.isNotEmpty()) {
            SectionHeader("Sugestões Personalizadas")
            SuggestionsCard(
                suggestions = uiState.suggestedSessions,
                onSchedule = { suggestion, time -> 
                    viewModel.scheduleSession(suggestion, time)
                }
            )
            
            Spacer(Modifier.height(Tokens.SectionSpacing))
        }
        
        // Enhanced Scheduled Sessions
        SectionHeader("Sessões Agendadas")
        if (uiState.scheduledSessions.isEmpty()) {
            EmptyScheduleCard()
        } else {
            EnhancedSessionList(
                sessions = uiState.scheduledSessions,
                onComplete = { viewModel.completeSession(it) },
                onRemove = { viewModel.removeSession(it) }
            )
        }
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        
        // Performance Insights
        SectionHeader("Insights de Performance")
        PerformanceInsightsCard()
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        
        // Study Groups with enhanced features
        SectionHeader("Grupos de estudo")
        EnhancedStudyGroups()
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        
        // Smart Opponent Tracking
        SectionHeader("Rivais e Progresso")
        SmartOpponentTracking()
        
        // Bottom padding for bottom bar
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
private fun SmartDateSelector(
    selectedDate: LocalDate,
    onDateSelect: (LocalDate) -> Unit
) {
    val dates = (0..6).map { LocalDate.now().plusDays(it.toLong()) }
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(dates) { date ->
            SmartDateChip(
                date = date,
                isSelected = date == selectedDate,
                onSelect = { onDateSelect(date) }
            )
        }
    }
}

@Composable
private fun SmartDateChip(
    date: LocalDate,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val isToday = date == LocalDate.now()
    val isTomorrow = date == LocalDate.now().plusDays(1)
    
    val displayText = when {
        isToday -> "Hoje"
        isTomorrow -> "Amanhã"
        else -> date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
    }
    
    FilterChip(
        onClick = onSelect,
        label = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = displayText,
                    fontSize = 12.sp
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colorFromHex(Tokens.Primary),
            selectedLabelColor = Color.White,
            containerColor = colorFromHex(Tokens.SurfaceElevated),
            labelColor = colorFromHex(Tokens.Neutral)
        )
    )
}

@Composable
private fun SuggestionsCard(
    suggestions: List<SuggestedSession>,
    onSchedule: (SuggestedSession, java.time.LocalDateTime) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "IA",
                    tint = colorFromHex(Tokens.Primary),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Recomendações da IA",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Show top 3 suggestions
            suggestions.take(3).forEach { suggestion ->
                SuggestionItem(
                    suggestion = suggestion,
                    onSchedule = { onSchedule(suggestion, java.time.LocalDateTime.now().plusHours(1)) }
                )
                
                if (suggestion != suggestions.take(3).last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    suggestion: SuggestedSession,
    onSchedule: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = suggestion.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Priority badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (suggestion.priority) {
                            SessionPriority.CRITICAL -> colorFromHex(Tokens.Error)
                            SessionPriority.HIGH -> colorFromHex(Tokens.Warning)
                            SessionPriority.MEDIUM -> colorFromHex(Tokens.Primary)
                            SessionPriority.LOW -> colorFromHex(Tokens.Neutral)
                        }
                    ),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = suggestion.priority.name,
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            
            Text(
                text = suggestion.reason,
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Neutral)
            )
            
            Text(
                text = "Melhoria estimada: +${(suggestion.potentialImprovement * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Positive)
            )
        }
        
        IconButton(onClick = onSchedule) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Agendar",
                tint = colorFromHex(Tokens.Primary)
            )
        }
    }
}

@Composable
private fun EmptyScheduleCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EventAvailable,
                contentDescription = "Agenda vazia",
                tint = colorFromHex(Tokens.Neutral),
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Nenhuma sessão agendada",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            
            Text(
                text = "Use as sugestões acima para otimizar seu treino",
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
private fun EnhancedSessionList(
    sessions: List<ScheduledSession>,
    onComplete: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        sessions.forEach { session ->
            EnhancedSessionCard(
                session = session,
                onComplete = { onComplete(session.id) },
                onRemove = { onRemove(session.id) }
            )
        }
    }
}

@Composable
private fun EnhancedSessionCard(
    session: ScheduledSession,
    onComplete: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (session.isCompleted) {
                colorFromHex(Tokens.Positive).copy(alpha = 0.1f)
            } else {
                colorFromHex(Tokens.SurfaceElevated)
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (session.sessionType) {
                                SessionType.DRILL_PRACTICE -> Icons.Default.FitnessCenter
                                SessionType.CONCEPT_STUDY -> Icons.Default.School
                                SessionType.HAND_REVIEW -> Icons.Default.Analytics
                                SessionType.MENTAL_GAME -> Icons.Default.Psychology
                                SessionType.TOURNAMENT_PREP -> Icons.Default.EmojiEvents
                            },
                            contentDescription = session.sessionType.name,
                            tint = if (session.isCompleted) {
                                colorFromHex(Tokens.Positive)
                            } else {
                                colorFromHex(Tokens.Primary)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (session.isOptimal) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Horário ótimo",
                                tint = colorFromHex(Tokens.Warning),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = session.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Horário",
                            tint = colorFromHex(Tokens.Neutral),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = session.scheduledTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            style = MaterialTheme.typography.bodySmall,
                            color = colorFromHex(Tokens.Neutral)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Duração",
                            tint = colorFromHex(Tokens.Neutral),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "${session.duration}min",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorFromHex(Tokens.Neutral)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "+${(session.estimatedImprovement * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorFromHex(Tokens.Positive),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Action buttons
                Column {
                    if (!session.isCompleted) {
                        IconButton(onClick = onComplete) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Concluir",
                                tint = colorFromHex(Tokens.Positive)
                            )
                        }
                    }
                    
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remover",
                            tint = colorFromHex(Tokens.Negative)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PerformanceInsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Insights",
                    tint = colorFromHex(Tokens.Primary),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Insights da Semana",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InsightMetric(
                    value = "89%",
                    label = "Precisão",
                    trend = "+5%",
                    isPositive = true
                )
                InsightMetric(
                    value = "4.2h",
                    label = "Tempo de Estudo",
                    trend = "+12min",
                    isPositive = true
                )
                InsightMetric(
                    value = "12",
                    label = "Streak",
                    trend = "+3 dias",
                    isPositive = true
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "💡 Dica: Seus melhores horários de performance são entre 19h-21h",
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Primary)
            )
        }
    }
}

@Composable
private fun InsightMetric(
    value: String,
    label: String,
    trend: String,
    isPositive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colorFromHex(Tokens.Neutral)
        )
        Text(
            text = trend,
            style = MaterialTheme.typography.bodySmall,
            color = if (isPositive) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Negative),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EnhancedStudyGroups() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = group.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${group.members} membros ativos",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorFromHex(Tokens.Neutral)
                            )
                        }
                        
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = group.nextSession,
                                style = MaterialTheme.typography.labelSmall,
                                color = colorFromHex(Tokens.Primary),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Próxima sessão",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorFromHex(Tokens.Neutral)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { /* TODO: Join group */ },
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "Entrar no grupo",
                                color = colorFromHex(Tokens.Primary)
                            )
                        }
                        
                        TextButton(
                            onClick = { /* TODO: View details */ }
                        ) {
                            Text(
                                text = "Ver detalhes",
                                color = colorFromHex(Tokens.Neutral)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmartOpponentTracking() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                    // Avatar with status
                    Box {
                        Text(
                            text = opponent.avatar,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.size(48.dp)
                        )
                        
                        // Online status dot
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = colorFromHex(Tokens.Positive),
                                    shape = androidx.compose.foundation.shape.CircleShape
                                )
                                .align(Alignment.TopEnd)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = opponent.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Win rate: ${opponent.winRate}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorFromHex(Tokens.Positive)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "• ${opponent.lastPlayed}",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorFromHex(Tokens.Neutral)
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = { /* TODO: Challenge opponent */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sports,
                            contentDescription = "Desafiar",
                            tint = colorFromHex(Tokens.Primary)
                        )
                    }
                }
            }
        }
    }
}
