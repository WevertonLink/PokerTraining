package com.pokertrainer.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.data.SampleData
import com.pokertrainer.domain.models.DrillCategory
import com.pokertrainer.domain.models.TrainingDrill
import com.pokertrainer.ui.components.SectionHeader
import com.pokertrainer.ui.components.TopGreetingBar
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun PracticeHomeScreen(
    userName: String = "Alex",
    onDrillClick: (TrainingDrill) -> Unit = {},
    onCategoryClick: (DrillCategory) -> Unit = {},
    onPostflopStart: (String) -> Unit = {},
    onHandReadingStart: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        TopGreetingBar(userName)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Estatísticas rápidas
        QuickStatsSection()
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Categorias de drill
        SectionHeader("Categorias de Treino")
        Spacer(modifier = Modifier.height(12.dp))
        DrillCategoriesGrid(onCategoryClick)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Treinos Avançados
        SectionHeader("Treinos Avançados")
        Spacer(modifier = Modifier.height(12.dp))
        
        // Treino pós-flop
        ActionCard(
            title = "Simulação Pós-flop",
            icon = Icons.Default.Animation,
            description = "Tome decisões em situações complexas de flop, turn e river",
            onStart = { onPostflopStart(java.util.UUID.randomUUID().toString()) }
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Leitura de mãos
        ActionCard(
            title = "Leitura de Mãos",
            icon = Icons.Default.Psychology,
            description = "Desenvolva sua habilidade de identificar ranges oponentes",
            onStart = onHandReadingStart
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Drills recomendados
        SectionHeader("Drills Recomendados")
        Spacer(modifier = Modifier.height(12.dp))
        RecommendedDrillsList(
            drills = SampleData.preflopDrills,
            onDrillClick = onDrillClick
        )
    }
}

@Composable
private fun QuickStatsSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            title = "Drills Concluídos",
            value = "47",
            icon = Icons.Default.CheckCircle,
            color = colorFromHex(Tokens.Positive),
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            title = "Streak Atual",
            value = "12 dias",
            icon = Icons.Default.LocalFireDepartment,
            color = colorFromHex(Tokens.Primary),
            modifier = Modifier.weight(1f)
        )
        QuickStatCard(
            title = "Precisão",
            value = "87%",
            icon = Icons.Default.TrendingUp,
            color = colorFromHex(Tokens.Positive),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            
            Column {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
        }
    }
}

@Composable
private fun DrillCategoriesGrid(onCategoryClick: (DrillCategory) -> Unit) {
    val categories = listOf(
        Pair(DrillCategory.PREFLOP, Icons.Default.Casino),
        Pair(DrillCategory.POSTFLOP, Icons.Default.ViewStream),
        Pair(DrillCategory.HAND_READING, Icons.Default.EmojiEvents),
        Pair(DrillCategory.BANKROLL_MANAGEMENT, Icons.Default.AttachMoney)
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        categories.chunked(2).forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowCategories.forEach { (category, icon) ->
                    DrillCategoryCard(
                        category = category,
                        icon = icon,
                        onClick = { onCategoryClick(category) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Se só há uma categoria na linha, adiciona um espaço
                if (rowCategories.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DrillCategoryCard(
    category: DrillCategory,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryName = when (category) {
        DrillCategory.PREFLOP -> "Pre-flop"
        DrillCategory.POSTFLOP -> "Post-flop"
        DrillCategory.HAND_READING -> "Hand Reading"
        DrillCategory.BANKROLL_MANAGEMENT -> "Bankroll"
    }
    
    val categoryColor = when (category) {
        DrillCategory.PREFLOP -> Tokens.Primary
        DrillCategory.POSTFLOP -> Tokens.Positive
        DrillCategory.HAND_READING -> "#FFB800"
        DrillCategory.BANKROLL_MANAGEMENT -> Tokens.Negative
    }
    
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = categoryName,
                tint = colorFromHex(categoryColor),
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = categoryName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun RecommendedDrillsList(
    drills: List<TrainingDrill>,
    onDrillClick: (TrainingDrill) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(drills) { drill ->
            DrillCard(
                drill = drill,
                onClick = { onDrillClick(drill) }
            )
        }
    }
}

@Composable
private fun DrillCard(
    drill: TrainingDrill,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
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
                    Text(
                        text = drill.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = drill.description,
                        fontSize = 12.sp,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    // Estrelas de dificuldade
                    DifficultyStars(difficulty = drill.difficulty)
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Status de conclusão
                    if (drill.completed) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Concluído",
                            tint = colorFromHex(Tokens.Positive),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Duração",
                        tint = colorFromHex(Tokens.Neutral),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${drill.duration} min",
                        fontSize = 11.sp,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
                
                Text(
                    text = drill.category.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontSize = 10.sp,
                    color = colorFromHex(Tokens.Primary),
                    modifier = Modifier
                        .background(
                            color = colorFromHex(Tokens.Primary).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun DifficultyStars(difficulty: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < difficulty) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Estrela ${index + 1}",
                tint = if (index < difficulty) colorFromHex("#FFB800") else colorFromHex(Tokens.Neutral),
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    icon: ImageVector,
    description: String,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStart() },
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = colorFromHex(Tokens.Primary),
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Iniciar",
                tint = colorFromHex(Tokens.Neutral),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
