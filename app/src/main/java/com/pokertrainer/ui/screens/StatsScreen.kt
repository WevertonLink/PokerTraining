package com.pokertrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokertrainer.data.SampleData
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun StatsScreen(
    onBackPressed: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        // Header com botão de voltar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
            Text(
                text = "Estatísticas Detalhadas",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Performance Geral
        SectionHeader("Performance Geral")
        PerformanceCard()
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        
        // Estatísticas por Posição
        SectionHeader("Por Posição")
        PositionStatsGrid()
        
        Spacer(Modifier.height(Tokens.SectionSpacing))
        
        // Histórico de Sessões
        SectionHeader("Histórico de Sessões")
        SessionHistoryList()
        
        // Add some bottom padding for the bottom bar
        Spacer(Modifier.height(100.dp))
    }
}

@Composable
fun PerformanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(Tokens.CardElevation)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BB/100",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    Text(
                        text = "5.2",
                        style = MaterialTheme.typography.displayLarge,
                        color = colorFromHex(Tokens.Positive)
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Tendência",
                    tint = colorFromHex(Tokens.Positive),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PerformanceMetric("ROI", "18.4%", Tokens.Positive)
                PerformanceMetric("ITM", "32%", Tokens.Primary)
                PerformanceMetric("Profit", "$2,840", Tokens.Positive)
            }
        }
    }
}

@Composable
fun PerformanceMetric(label: String, value: String, colorHex: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorFromHex(Tokens.Neutral)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = colorFromHex(colorHex)
        )
    }
}

@Composable
fun PositionStatsGrid() {
    val positions = listOf(
        Triple("UTG", "12%", 0.3f),
        Triple("MP", "18%", 0.45f),
        Triple("CO", "28%", 0.7f),
        Triple("BTN", "42%", 0.85f),
        Triple("SB", "35%", 0.6f),
        Triple("BB", "22%", 0.4f)
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing)
    ) {
        positions.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing)
            ) {
                row.forEach { (position, vpip, progress) ->
                    PositionStatCard(
                        position = position,
                        vpip = vpip,
                        progress = progress,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PositionStatCard(
    position: String,
    vpip: String,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = position,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorFromHex(Tokens.Neutral)
                )
                Text(
                    text = vpip,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = colorFromHex(Tokens.Primary),
                trackColor = colorFromHex(Tokens.Border),
            )
        }
    }
}

@Composable
fun SessionHistoryList() {
    val sessions = listOf(
        Triple("Hoje", "+$340", Tokens.Positive),
        Triple("Ontem", "-$120", Tokens.Negative),
        Triple("2 dias atrás", "+$580", Tokens.Positive),
        Triple("3 dias atrás", "+$225", Tokens.Positive),
        Triple("4 dias atrás", "-$85", Tokens.Negative)
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(Tokens.ItemSpacing)
    ) {
        sessions.forEach { (date, result, colorHex) ->
            SessionHistoryCard(date, result, colorHex)
        }
    }
}

@Composable
fun SessionHistoryCard(date: String, result: String, colorHex: String) {
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = result,
                style = MaterialTheme.typography.titleMedium,
                color = colorFromHex(colorHex)
            )
        }
    }
}
