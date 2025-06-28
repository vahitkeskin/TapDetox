package com.vahitkeskin.tapdetox.screens

import android.content.Intent
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.vahitkeskin.tapdetox.R
import com.vahitkeskin.tapdetox.model.AppInfoWithUsage
import com.vahitkeskin.tapdetox.ui.theme.BackgroundDark
import com.vahitkeskin.tapdetox.ui.theme.BorderColor
import com.vahitkeskin.tapdetox.ui.theme.CardBackground
import com.vahitkeskin.tapdetox.ui.theme.DonutGray
import com.vahitkeskin.tapdetox.utils.TapDetoxPreview
import com.vahitkeskin.tapdetox.utils.formatMillisToHms
import com.vahitkeskin.tapdetox.utils.getAllUsedAppsSinceMidnight
import com.vahitkeskin.tapdetox.utils.isUsageAccessGranted
import kotlinx.coroutines.delay

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var appList by remember { mutableStateOf<List<AppInfoWithUsage>>(emptyList()) }
    var hasPermission by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        delay(100)
        hasPermission = isUsageAccessGranted(context)
        if (hasPermission == true) {
            appList = getAllUsedAppsSinceMidnight(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (hasPermission) {
            true -> {
                UsageProgressDonutChart(apps = appList)
            }
            false -> {
                PermissionRequestSection()
            }

            else -> {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun PermissionRequestSection() {
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Uygulama kullanım izni verilmedi.", color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            // Kullanıcıyı kullanım erişimi izin ekranına yönlendir
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            context.startActivity(intent)
        }) {
            Text("İzin Ver")
        }
    }
}

@Composable
fun UsageProgressDonutChart(
    apps: List<AppInfoWithUsage> = emptyList()
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.app_name),
            color = Color.White,
            fontSize = 32.sp
        )
        // Donut Grafik Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Gece 12’den beri kullanım oranı",
                    color = Color.White,
                    fontSize = 22.sp
                )
                val now = System.currentTimeMillis()
                val calendar = java.util.Calendar.getInstance().apply {
                    timeInMillis = now
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                val midnight = calendar.timeInMillis

                val elapsedTime = (now - midnight).toFloat().coerceAtLeast(1f) // ms olarak
                val totalUsage = apps.sumOf { it.usageMillis }.toFloat()

                // Animasyon ilerlemesi
                val animatedProgress by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .aspectRatio(1f)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Butt)
                        var startAngle = -90f
                        val totalElapsed = elapsedTime.coerceAtLeast(1f)

                        apps.forEach { app ->
                            val sweepAngle = (app.usageMillis / totalElapsed) * 360f * animatedProgress
                            val color = getDominantColorFromAppIcon(app.icon)
                            println("This is size where: ${color.value} and ${apps.size}")

                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = stroke
                            )
                            startAngle += sweepAngle
                        }

                        val usedSweepAngle = (totalUsage / totalElapsed).coerceAtMost(1f) * 360f * animatedProgress
                        if (usedSweepAngle < 360f) {
                            drawArc(
                                color = DonutGray,
                                startAngle = startAngle,
                                sweepAngle = 360f - usedSweepAngle,
                                useCenter = false,
                                style = stroke
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val usagePercent = (totalUsage / elapsedTime).coerceIn(0f, 1f)
                        Text(
                            text = "${(usagePercent * 100).format(1)} %",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gece 12'den beri",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kullanılan toplam süre ve kalan süre bilgisi
                val usedHms = formatMillisToHms(totalUsage.toLong())
                val remainingHms = formatMillisToHms((elapsedTime - totalUsage).toLong())

                Text(
                    text = "Kullanılan zaman: $usedHms",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "Kalan zaman: $remainingHms",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // En çok kullanılan uygulamalar Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "En çok kullanılan ${apps.size} uygulama",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                val context = LocalContext.current

                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .fillMaxWidth()
                ) {
                    items(apps) { app ->
                        val bitmap = remember(app.icon) { app.icon.toBitmap() }
                        val dominantColor = remember { getDominantColorFromAppIcon(app.icon) }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Image(
                                painter = BitmapPainter(bitmap.asImageBitmap()),
                                contentDescription = app.name,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "${app.name} (${formatMillisToHms(app.usageMillis)})",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

            }
        }
    }
}

fun getDominantColorFromAppIcon(drawable: Drawable): Color {
    val bitmap = drawable.toBitmap(48, 48)
    val colorCountMap = mutableMapOf<Int, Int>()

    for (x in 0 until bitmap.width) {
        for (y in 0 until bitmap.height) {
            val pixel = bitmap.getPixel(x, y)
            val alpha = (pixel shr 24) and 0xFF
            if (alpha > 128) {
                // Renk yoğunluğu artır
                colorCountMap[pixel] = colorCountMap.getOrDefault(pixel, 0) + 1
            }
        }
    }

    val dominantColorInt = colorCountMap.maxByOrNull { it.value }?.key ?: 0xFF888888.toInt()
    return Color(dominantColorInt)
}

// Yardımcı extension
fun Float.format(digits: Int) = "%.${digits}f".format(this)

@TapDetoxPreview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}