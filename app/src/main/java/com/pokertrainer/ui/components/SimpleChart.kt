package com.pokertrainer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pokertrainer.domain.models.ProgressPoint
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex
import kotlin.math.max
import kotlin.math.min

@Composable
fun SimpleChart(
    data: List<ProgressPoint>,
    modifier: Modifier = Modifier,
    title: String = "Progresso"
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (data.isNotEmpty()) {
                ChartContent(data = data)
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sem dados disponíveis",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartContent(data: List<ProgressPoint>) {
    // Preparar dados
    val maxAccuracy = data.maxOfOrNull { it.accuracy } ?: 1.0
    val minAccuracy = data.minOfOrNull { it.accuracy } ?: 0.0
    val range = maxAccuracy - minAccuracy
    val normalizedRange = if (range == 0.0) 1.0 else range
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        // Gráfico de linha
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            if (data.size >= 2) {
                drawLineChart(
                    data = data,
                    minValue = minAccuracy,
                    range = normalizedRange,
                    canvasWidth = size.width,
                    canvasHeight = size.height
                )
            }
        }
        
        // Indicadores de valor
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (data.isNotEmpty()) {
                ChartIndicator(
                    label = "Início",
                    value = "${(data.first().accuracy * 100).toInt()}%"
                )
                ChartIndicator(
                    label = "Atual",
                    value = "${(data.last().accuracy * 100).toInt()}%"
                )
            }
        }
    }
}

@Composable
private fun ChartIndicator(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = colorFromHex(Tokens.Primary),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colorFromHex(Tokens.Neutral)
        )
    }
}

private fun DrawScope.drawLineChart(
    data: List<ProgressPoint>,
    minValue: Double,
    range: Double,
    canvasWidth: Float,
    canvasHeight: Float
) {
    val strokeWidth = 3.dp.toPx()
    val primaryColor = Color(0xFF7C5CFF)
    val padding = 20.dp.toPx()
    
    val chartWidth = canvasWidth - (padding * 2)
    val chartHeight = canvasHeight - (padding * 2)
    
    // Calcular pontos
    val points = data.mapIndexed { index, point ->
        val x = padding + (index.toFloat() / (data.size - 1).toFloat()) * chartWidth
        val normalizedY = if (range == 0.0) 0.5 else (point.accuracy - minValue) / range
        val y = padding + chartHeight - (normalizedY.toFloat() * chartHeight)
        Offset(x, y)
    }
    
    // Desenhar linha
    if (points.size >= 2) {
        val path = Path()
        path.moveTo(points.first().x, points.first().y)
        
        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
        }
        
        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = strokeWidth)
        )
    }
    
    // Desenhar pontos
    points.forEach { point ->
        drawCircle(
            color = primaryColor,
            radius = 4.dp.toPx(),
            center = point
        )
    }
}

@Composable
fun PerformanceMetrics(
    accuracy: Double,
    improvement: Double,
    streak: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MetricItem(
                value = "${(accuracy * 100).toInt()}%",
                label = "Precisão",
                color = colorFromHex(Tokens.Primary)
            )
            
            MetricItem(
                value = "${if (improvement >= 0) "+" else ""}${(improvement * 100).toInt()}%",
                label = "Melhoria",
                color = if (improvement >= 0) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Negative)
            )
            
            MetricItem(
                value = "${streak}d",
                label = "Sequência",
                color = colorFromHex(Tokens.Warning)
            )
        }
    }
}

@Composable
private fun MetricItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colorFromHex(Tokens.Neutral)
        )
    }
}
