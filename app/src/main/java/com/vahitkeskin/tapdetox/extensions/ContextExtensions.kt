package com.vahitkeskin.tapdetox.extensions

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type

@Composable
fun Context.statusBarHeightDp(): Dp {
    val density = LocalDensity.current
    val heightPx = getSystemBarHeightPx(Type.statusBars())
    return with(density) { heightPx.toDp() }
}

@Composable
fun Context.navigationBarHeightDp(): Dp {
    val density = LocalDensity.current
    val heightPx = getSystemBarHeightPx(Type.navigationBars())
    return with(density) { heightPx.toDp() }
}

private fun Context.getSystemBarHeightPx(type: Int): Int {
    val activity = this as? Activity ?: return 0
    val decorView = activity.window.decorView
    val windowInsets = ViewCompat.getRootWindowInsets(decorView) ?: return 0

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // API 30+ yeni, desteklenen API
        windowInsets.getInsets(type).let {
            when (type) {
                Type.statusBars() -> it.top
                Type.navigationBars() -> it.bottom
                else -> 0
            }
        }
    } else {
        // API 29 ve altı, zorunlu eski API (deprecated)
        when (type) {
            Type.statusBars() -> windowInsets.systemWindowInsetTop
            Type.navigationBars() -> windowInsets.systemWindowInsetBottom
            else -> 0
        }
    }
}