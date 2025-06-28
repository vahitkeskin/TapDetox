package com.vahitkeskin.tapdetox.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.vahitkeskin.tapdetox.model.AppInfoWithUsage
import com.vahitkeskin.tapdetox.utils.formatMillisToHms
import com.vahitkeskin.tapdetox.utils.getAllUsedAppsSinceMidnight
import com.vahitkeskin.tapdetox.utils.isUsageAccessGranted
import com.vahitkeskin.tapdetox.utils.requestUsageAccessPermission

@Composable
fun RecentAppsUsageScreen() {
    val context = LocalContext.current
    var appList by remember { mutableStateOf<List<AppInfoWithUsage>>(emptyList()) }
    var hasUsagePermission by remember { mutableStateOf<Boolean?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        hasUsagePermission = isUsageAccessGranted(context)
        if (hasUsagePermission == true) {
            appList = getAllUsedAppsSinceMidnight(context)
        }
        isLoading = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            hasUsagePermission == false -> {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Uygulama kullanım verilerine erişim izni verilmemiş.")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { requestUsageAccessPermission(context) }) {
                        Text("İzin Ver")
                    }
                }
            }

            hasUsagePermission == true -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Gece 12'den beri kullanım: ${appList.size} uygulama")
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(appList.take(3)) { app ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                val bitmap = remember(app.icon) { app.icon.toBitmap() }
                                Image(
                                    painter = BitmapPainter(bitmap.asImageBitmap()),
                                    contentDescription = app.name,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "${app.name} (${formatMillisToHms(app.usageMillis)})"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}