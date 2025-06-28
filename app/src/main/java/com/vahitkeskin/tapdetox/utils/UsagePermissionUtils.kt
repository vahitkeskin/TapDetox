package com.vahitkeskin.tapdetox.utils

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Process
import android.provider.Settings
import com.vahitkeskin.tapdetox.model.AppInfoWithUsage

fun isUsageAccessGranted(context: Context): Boolean {
    val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOpsManager.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    )
    return mode == AppOpsManager.MODE_ALLOWED
}

fun getAllUsedAppsSinceMidnight(context: Context): List<AppInfoWithUsage> {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val now = System.currentTimeMillis()
    val calendar = java.util.Calendar.getInstance().apply {
        timeInMillis = now
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    val midnight = calendar.timeInMillis

    val usageStats = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY,
        midnight,
        now
    )

    val pm = context.packageManager

    return usageStats
        .filter { it.totalTimeInForeground > 0 } // sadece kullanılanlar
        .mapNotNull { stat ->
            try {
                val appInfo = pm.getApplicationInfo(stat.packageName, 0)
                val appName = pm.getApplicationLabel(appInfo).toString()
                val appIcon = pm.getApplicationIcon(appInfo)
                val totalTime = stat.totalTimeInForeground
                AppInfoWithUsage(appName, appIcon, totalTime)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }
        .sortedByDescending { it.usageMillis }
        .distinctBy { it.name }
}