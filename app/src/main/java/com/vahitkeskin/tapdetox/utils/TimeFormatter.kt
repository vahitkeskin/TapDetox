package com.vahitkeskin.tapdetox.utils

fun formatMillisToHms(millis: Long): String {
    val seconds = millis / 1000 % 60
    val minutes = millis / (1000 * 60) % 60
    val hours = millis / (1000 * 60 * 60)
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
