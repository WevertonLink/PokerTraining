package com.pokertrainer.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AdvancedAnalyticsCard(
    title: String,
    subtitle: String,
    value: String,
    trend: AnalyticsTrend,
    chartData: List<Float>,
    color: Color = colorFromHex(Tokens.Primary),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
                
                TrendIndicator(trend = trend)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            MiniSparklineChart(
                data = chartData,
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }
    }
}

@Composable
fun PerformanceRadarChart(
    metrics: List<RadarMetric>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🎯 Análise de Performance",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                RadarChartCanvas(
                    metrics = metrics,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Legend
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(metrics) { metric ->
                    RadarLegendItem(metric = metric)
                }
            }
        }
    }
}

@Composable
private fun RadarChartCanvas(
    metrics: List<RadarMetric>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = (size.minDimension / 2) * 0.8f
        val angleStep = 360f / metrics.size
        
        // Draw background circles
        for (i in 1..5) {
            val circleRadius = radius * (i / 5f)
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                radius = circleRadius,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }
        
        // Draw axes
        metrics.forEachIndexed { index, _ ->
            val angle = Math.toRadians((angleStep * index - 90).toDouble())
            val endPoint = Offset(
                center.x + (radius * cos(angle)).toFloat(),
                center.y + (radius * sin(angle)).toFloat()
            )
            
            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = center,
                end = endPoint,
                strokeWidth = 1.dp.toPx()
            )
        }
        
        // Draw performance polygon
        val path = Path()
        metrics.forEachIndexed { index, metric ->
            val angle = Math.toRadians((angleStep * index - 90).toDouble())
            val distance = radius * (metric.value / 100f)
            val point = Offset(
                center.x + (distance * cos(angle)).toFloat(),
                center.y + (distance * sin(angle)).toFloat()
            )
            
            if (index == 0) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }
        path.close()
        
        // Fill the polygon
        drawPath(
            path = path,
            color = colorFromHex(Tokens.Primary).copy(alpha = 0.3f)
        )
        
        // Draw the outline
        drawPath(
            path = path,
            color = colorFromHex(Tokens.Primary),
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Draw data points
        metrics.forEachIndexed { index, metric ->
            val angle = Math.toRadians((angleStep * index - 90).toDouble())
            val distance = radius * (metric.value / 100f)
            val point = Offset(
                center.x + (distance * cos(angle)).toFloat(),
                center.y + (distance * sin(angle)).toFloat()
            )
            
            drawCircle(
                color = colorFromHex(Tokens.Primary),
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
private fun RadarLegendItem(metric: RadarMetric) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = metric.color,
                    shape = CircleShape
                )
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = metric.label,
            style = MaterialTheme.typography.labelSmall,
            color = colorFromHex(Tokens.Neutral)
        )
    }
}

@Composable
fun HeatmapCard(
    title: String,
    data: List<List<Float>>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HeatmapGrid(
                data = data,
                labels = labels,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun HeatmapGrid(
    data: List<List<Float>>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.height(200.dp)) {
        val cellWidth = size.width / data[0].size
        val cellHeight = size.height / data.size
        
        data.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, value ->
                val intensity = value / 100f
                val color = colorFromHex(Tokens.Primary).copy(alpha = intensity)
                
                drawRect(
                    color = color,
                    topLeft = Offset(colIndex * cellWidth, rowIndex * cellHeight),
                    size = Size(cellWidth, cellHeight)
                )
                
                // Draw border
                drawRect(
                    color = Color.White.copy(alpha = 0.1f),
                    topLeft = Offset(colIndex * cellWidth, rowIndex * cellHeight),
                    size = Size(cellWidth, cellHeight),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun MiniSparklineChart(
    data: List<Float>,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        
        val maxValue = data.maxOrNull() ?: 1f
        val minValue = data.minOrNull() ?: 0f
        val valueRange = maxValue - minValue
        
        val path = Path()
        val stepWidth = size.width / (data.size - 1)
        
        data.forEachIndexed { index, value ->
            val x = index * stepWidth
            val normalizedValue = if (valueRange > 0) (value - minValue) / valueRange else 0.5f
            val y = size.height - (normalizedValue * size.height)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        
        // Draw gradient fill
        val fillPath = Path().apply {
            addPath(path)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        
        val gradient = Brush.verticalGradient(
            colors = listOf(
                color.copy(alpha = 0.3f),
                Color.Transparent
            )
        )
        
        drawPath(
            path = fillPath,
            brush = gradient
        )
    }
}

@Composable
private fun TrendIndicator(trend: AnalyticsTrend) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = when (trend.direction) {
                TrendDirection.UP -> Icons.Default.TrendingUp
                TrendDirection.DOWN -> Icons.Default.TrendingDown
                TrendDirection.FLAT -> Icons.Default.TrendingFlat
            },
            contentDescription = trend.direction.name,
            tint = when (trend.direction) {
                TrendDirection.UP -> colorFromHex(Tokens.Positive)
                TrendDirection.DOWN -> colorFromHex(Tokens.Error)
                TrendDirection.FLAT -> colorFromHex(Tokens.Neutral)
            },
            modifier = Modifier.size(16.dp)
        )
        
        Text(
            text = "${if (trend.direction == TrendDirection.UP) "+" else if (trend.direction == TrendDirection.DOWN) "-" else ""}${trend.percentage}%",
            style = MaterialTheme.typography.labelMedium,
            color = when (trend.direction) {
                TrendDirection.UP -> colorFromHex(Tokens.Positive)
                TrendDirection.DOWN -> colorFromHex(Tokens.Error)
                TrendDirection.FLAT -> colorFromHex(Tokens.Neutral)
            },
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SessionStreakVisualization(
    streaks: List<StreakData>,
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔥 Histórico de Sequências",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Atual: $currentStreak dias",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorFromHex(Tokens.Warning),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(streaks) { streak ->
                    StreakBar(
                        streak = streak,
                        isActive = streak.isActive
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakBar(
    streak: StreakData,
    isActive: Boolean
) {
    val height by animateDpAsState(
        targetValue = (8 + streak.length * 2).dp.coerceAtMost(60.dp),
        animationSpec = tween(500),
        label = "Streak Height"
    )
    
    Box(
        modifier = Modifier
            .width(8.dp)
            .height(height)
            .background(
                color = if (isActive) 
                    colorFromHex(Tokens.Warning) 
                else 
                    colorFromHex(Tokens.Primary).copy(alpha = 0.6f),
                shape = RoundedCornerShape(4.dp)
            )
    )
}

// Data classes for analytics components
data class AnalyticsTrend(
    val direction: TrendDirection,
    val percentage: Float
)

enum class TrendDirection {
    UP, DOWN, FLAT
}

data class RadarMetric(
    val label: String,
    val value: Float, // 0-100
    val color: Color
)

data class StreakData(
    val length: Int,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean = false
)
