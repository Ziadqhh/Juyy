package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MarbleItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val items by viewModel.items.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "عودة", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "الإحصائيات",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("لا توجد بيانات لعرض الإحصائيات")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                SummaryStats(items)
                Spacer(modifier = Modifier.height(24.dp))
                
                // Group by type for charts
                val groupedByType = items.groupBy { it.type.lowercase(Locale.ROOT) }
                val areaData = groupedByType.mapValues { entry -> entry.value.sumOf { it.totalArea } }.toList()
                val countData = groupedByType.mapValues { entry -> entry.value.sumOf { it.count }.toDouble() }.toList()

                Text("توزيع المساحات (م²)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                BarChart(data = areaData, color = MaterialTheme.colorScheme.primary)
                
                Spacer(modifier = Modifier.height(24.dp))

                Text("توزيع الأعداد", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                BarChart(data = countData, color = MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
fun SummaryStats(items: List<MarbleItem>) {
    val totalArea = items.sumOf { it.totalArea }
    val avgArea = if (items.isNotEmpty()) totalArea / items.size else 0.0
    val largest = items.maxByOrNull { it.totalArea }
    val smallest = items.minByOrNull { it.totalArea }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("متوسط مساحة الصنف: ${String.format("%.2f", avgArea)} م²")
            if (largest != null) {
                Text("أكبر صنف مساحة: ${largest.type} (${String.format("%.2f", largest.totalArea)} م²)")
            }
            if (smallest != null) {
                Text("أصغر صنف مساحة: ${smallest.type} (${String.format("%.2f", smallest.totalArea)} م²)")
            }
        }
    }
}

@Composable
fun BarChart(data: List<Pair<String, Double>>, color: Color) {
    if (data.isEmpty()) return
    val maxVal = data.maxOf { it.second }
    if (maxVal == 0.0) return

    val textMeasurer = rememberTextMeasurer()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val barWidth = size.width / (data.size * 2)
        val maxBarHeight = size.height - 40.dp.toPx()

        data.forEachIndexed { index, pair ->
            val (label, value) = pair
            val barHeight = ((value / maxVal) * maxBarHeight).toFloat()
            val xOffset = index * barWidth * 2 + barWidth / 2

            // Draw Bar
            drawRect(
                brush = SolidColor(color),
                topLeft = Offset(xOffset, size.height - barHeight - 20.dp.toPx()),
                size = Size(barWidth, barHeight)
            )

            // Draw Value
            val valText = String.format("%.1f", value)
            val textLayoutResultVal = textMeasurer.measure(
                text = valText,
                style = TextStyle(color = onSurfaceColor, fontSize = 10.sp)
            )
            drawText(
                textLayoutResult = textLayoutResultVal,
                topLeft = Offset(xOffset + barWidth / 2 - textLayoutResultVal.size.width / 2, size.height - barHeight - 20.dp.toPx() - 15.dp.toPx())
            )

            // Draw Label
            val textLayoutResult = textMeasurer.measure(
                text = label.take(6), // Truncate long labels
                style = TextStyle(color = onSurfaceColor, fontSize = 10.sp)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(xOffset + barWidth / 2 - textLayoutResult.size.width / 2, size.height - 15.dp.toPx())
            )
        }
    }
}
