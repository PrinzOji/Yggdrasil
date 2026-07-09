package com.ojiem.yggdrasil.ui.screens.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojiem.yggdrasil.ui.theme.NatureBackground
import com.ojiem.yggdrasil.ui.theme.NatureMint
import com.ojiem.yggdrasil.ui.theme.NatureSkyBlue
import com.ojiem.yggdrasil.ui.theme.glassmorphism

@Composable
fun AnalyticsScreen() {
    NatureBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(56.dp))
            
            Text("Ecosystem Analytics", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Text("Pulse of the World Tree network", color = NatureMint.copy(alpha = 0.7f), fontSize = 14.sp)

            Spacer(Modifier.height(32.dp))

            AnalyticsCard("Network Price Distribution") {
                PieChart(
                    data = listOf(0.4f, 0.3f, 0.2f, 0.1f),
                    colors = listOf(NatureMint, NatureSkyBlue, Color.Yellow, Color.Red)
                )
            }

            Spacer(Modifier.height(24.dp))

            AnalyticsCard("Your Contribution vs Global Avg") {
                ComparisonBar(userVal = 0.75f, avgVal = 0.55f)
            }

            Spacer(Modifier.height(24.dp))

            AnalyticsCard("Node Vitality (Last 7 Cycles)") {
                SimpleLineChart()
            }
            
            Spacer(Modifier.height(100.dp))
        }
    }
}

@Composable
fun AnalyticsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun ColumnScope.PieChart(data: List<Float>, colors: List<Color>) {
    Canvas(modifier = Modifier.size(150.dp).align(Alignment.CenterHorizontally)) {
        var startAngle = 0f
        data.forEachIndexed { index, value ->
            val sweepAngle = value * 360f
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun ComparisonBar(userVal: Float, avgVal: Float) {
    Column {
        ComparisonItem("You", userVal, NatureMint)
        Spacer(Modifier.height(12.dp))
        ComparisonItem("Average Seer", avgVal, NatureSkyBlue)
    }
}

@Composable
fun ComparisonItem(label: String, value: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            Text("${(value * 100).toInt()}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun SimpleLineChart() {
    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
        val points = listOf(Offset(0f, 80f), Offset(50f, 40f), Offset(100f, 60f), Offset(150f, 20f), Offset(200f, 50f), Offset(250f, 30f), Offset(300f, 10f))
        for (i in 0 until points.size - 1) {
            drawLine(
                color = NatureMint,
                start = points[i],
                end = points[i+1],
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}
