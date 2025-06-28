package com.vahitkeskin.tapdetox.model

import android.graphics.drawable.Drawable

data class AppInfoWithUsage(
    val name: String,
    val icon: Drawable,
    val usageMillis: Long
)
