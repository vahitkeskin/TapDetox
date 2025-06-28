package com.vahitkeskin.tapdetox.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.vahitkeskin.tapdetox.ui.theme.*

@Composable
fun ExactDonutChart(
    percentage: Int = 45
) {
    val segmentColors = listOf(
        DonutTurquoise,
        DonutYellow,
        DonutGray,
        SidebarBackground
    )
    val segmentSweepAngles = listOf(162f, 72f, 54f, 72f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()  // İçeriğe göre yükseklik
            .padding(horizontal = 16.dp)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Gece 12 den beri",
                color = Color.White,
                fontSize = 22.sp
            )
            Text(
                text = "3 Saat 25 Dakka",
                color = Color.White,
                fontSize = 16.sp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .aspectRatio(1f)  // Kare alan
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    var startAngle = -90f
                    val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)

                    for (i in segmentColors.indices) {
                        drawArc(
                            color = segmentColors[i],
                            startAngle = startAngle,
                            sweepAngle = segmentSweepAngles[i],
                            useCenter = false,
                            style = stroke
                        )
                        startAngle += segmentSweepAngles[i]
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$percentage %",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Other",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
            RecentAppsUsageScreen()
        }
    }
}
